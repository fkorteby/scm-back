package com.simple_cabinet_medical.Backend.Permission;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class BaseMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {

    private static final Logger logger = LoggerFactory.getLogger(BaseMethodSecurityExpressionHandler.class);

    @Autowired
    private CustomPermissionEvaluator customPermissionEvaluator;

    @Override
    public Object filter(Object filterTarget, Expression filterExpression, EvaluationContext ctx) {
        logger.debug("Filtering target of type: {}", filterTarget != null ? filterTarget.getClass().getName() : "null");

        if (ctx.getRootObject().getValue() instanceof SecurityExpressionRoot rootObject) {
            rootObject.setPermissionEvaluator(customPermissionEvaluator);
            Authentication authentication = rootObject.getAuthentication();

            // Handle null
            if (filterTarget == null) {
                return handleNullCase(filterExpression);
            }

            // Handle Optional
            if (filterTarget instanceof Optional<?> optional) {
                return filterOptional(optional, authentication);
            }

            // Handle Page - THIS IS THE CRITICAL PART
            if (filterTarget instanceof Page<?> page) {
                return filterPage(page, authentication);
            }

            // Handle other cases (Collection, Array, single object)
            return filterNonPageTarget(filterTarget, authentication);
        }
        return null;
    }

    private Object handleNullCase(Expression filterExpression) {
        if (filterExpression.getExpressionString().contains("Page")) {
            return Page.empty();
        }
        return new ArrayList<>();
    }

    private Object filterOptional(Optional<?> optional, Authentication authentication) {
        if (optional.isEmpty()) {
            return Optional.empty();
        }
        Object value = optional.get();
        if (customPermissionEvaluator.hasPermission(authentication, value, "READ")) {
            return optional;
        }
        return Optional.empty();
    }

    private Page<?> filterPage(Page<?> page, Authentication authentication) {
        // Filter the current page content
        List<Object> filteredContent = new ArrayList<>();
        for (Object item : page.getContent()) {
            if (customPermissionEvaluator.hasPermission(authentication, item, "READ")) {
                filteredContent.add(item);
            }
        }

        // Calculate the correct total elements (this is the tricky part)
        // Option 1: If you can afford to load all elements (not recommended for large datasets)
        // long totalElements = countAllWithPermission(authentication);

        // Option 2: Approximate by keeping original total if filtered content matches page size
        // or reduce if filtered content is smaller
        long totalElements = filteredContent.size() == page.getSize() ?
                page.getTotalElements() :
                page.getNumberOfElements();

        return new PageImpl<>(
                filteredContent,
                page.getPageable(),
                totalElements
        );
    }

    private Object filterNonPageTarget(Object target, Authentication authentication) {
        if (target instanceof Collection<?> collection) {
            return filterCollection(collection, authentication);
        }
        if (target.getClass().isArray()) {
            return filterCollection(Arrays.asList((Object[]) target), authentication);
        }
        if (customPermissionEvaluator.hasPermission(authentication, target, "READ")) {
            return target;
        }
        return null;
    }

    private Collection<?> filterCollection(Collection<?> collection, Authentication authentication) {
        Collection<Object> filtered = collection instanceof Set ? new HashSet<>() : new ArrayList<>();
        for (Object item : collection) {
            if (customPermissionEvaluator.hasPermission(authentication, item, "READ")) {
                filtered.add(item);
            }
        }
        return filtered;
    }

    // Optional: Implement this if you choose Option 1 for totalElements calculation
    /*
    private long countAllWithPermission(Authentication authentication) {
        // You would need to implement this method to count all items with READ permission
        // This might require a custom query in your repository
    }
    */
}
