package com.simple_cabinet_medical.Backend.Permission;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BaseMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {

    private final CustomPermissionEvaluator customPermissionEvaluator;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    public BaseMethodSecurityExpressionHandler(CustomPermissionEvaluator customPermissionEvaluator) {
        this.customPermissionEvaluator = customPermissionEvaluator;
        this.setPermissionEvaluator(customPermissionEvaluator);
    }

    @Override
    public Object filter(Object filterTarget, Expression filterExpression, EvaluationContext ctx) {
        if (filterTarget == null) {
            return List.of();
        }

        if (ctx.getRootObject().getValue() instanceof SecurityExpressionRoot) {
            ((SecurityExpressionRoot) ctx.getRootObject().getValue())
                    .setPermissionEvaluator(customPermissionEvaluator);
        }

        try {
            // Extract permission from expression
            String expressionString = filterExpression.getExpressionString();
            String permission = extractPermission(expressionString);
            Authentication auth = ((SecurityExpressionRoot) ctx.getRootObject().getValue()).getAuthentication();

            if (filterTarget instanceof Page<?>) {
                Page<?> page = (Page<?>) filterTarget;

                // Extract entity type from Page
                Class<?> entityType = determineEntityType(page);
                if (entityType == null) {
                    return filterPageContent(page, auth, permission);
                }

                // Find repository for entity type using naming convention
                Object repository = findRepositoryForEntity(entityType);
                if (repository == null) {
                    return filterPageContent(page, auth, permission);
                }

                // Get all items from repository
                List<?> allItems = invokeRepositoryFindAll(repository);
                if (allItems == null) {
                    return filterPageContent(page, auth, permission);
                }

                // Apply security filter to all items
                List<Object> filteredItems = allItems.stream()
                        .filter(item -> customPermissionEvaluator.hasPermission(auth, item, permission))
                        .collect(Collectors.toList());

                // Manually paginate filtered results
                Pageable pageable = page.getPageable();
                int start = (int) pageable.getOffset();
                int end = Math.min(start + pageable.getPageSize(), filteredItems.size());

                if (start >= filteredItems.size()) {
                    return new PageImpl<>(List.of(), pageable, filteredItems.size());
                }

                List<Object> pageContent = filteredItems.subList(start, end);
                return new PageImpl<>(pageContent, pageable, filteredItems.size());
            } else if (filterTarget instanceof Collection<?>) {
                Collection<?> collection = (Collection<?>) filterTarget;
                return collection.stream()
                        .filter(item -> customPermissionEvaluator.hasPermission(auth, item, permission))
                        .collect(Collectors.toList());
            } else if (filterTarget.getClass().isArray()) {
                // Use super implementation for arrays
                return super.filter(filterTarget, filterExpression, ctx);
            } else {
                // Single object
                return customPermissionEvaluator.hasPermission(auth, filterTarget, permission) ? filterTarget : null;
            }
        } catch (Exception ex) {
            return handleFilterError(filterTarget);
        }
    }

    private String extractPermission(String expressionString) {
        String permission = "READ";
        if (expressionString.contains("'")) {
            int start = expressionString.indexOf("'") + 1;
            int end = expressionString.indexOf("'", start);
            if (end > start) {
                permission = expressionString.substring(start, end);
            }
        }
        return permission;
    }

    private Object filterPageContent(Page<?> page, Authentication auth, String permission) {
        List<?> content = page.getContent();
        List<Object> filteredContent = new ArrayList<>();

        for (Object obj : content) {
            if (customPermissionEvaluator.hasPermission(auth, obj, permission)) {
                filteredContent.add(obj);
            }
        }

        return new PageImpl<>(filteredContent, page.getPageable(), filteredContent.size());
    }

    private Class<?> determineEntityType(Page<?> page) {
        try {
            if (!page.isEmpty()) {
                return page.getContent().get(0).getClass();
            }
            Field delegateField = findField(page.getClass(), "delegate");
            if (delegateField != null) {
                delegateField.setAccessible(true);
                Object delegate = delegateField.get(page);

                Field specField = findField(delegate.getClass(), "spec");
                if (specField != null) {
                    specField.setAccessible(true);
                    Object spec = specField.get(delegate);

                    Field entityTypeField = findField(spec.getClass(), "entityType");
                    if (entityTypeField != null) {
                        entityTypeField.setAccessible(true);
                        return (Class<?>) entityTypeField.get(spec);
                    }
                }
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private Field findField(Class<?> clazz, String fieldName) {
        Class<?> searchType = clazz;
        while (searchType != null) {
            for (Field field : searchType.getDeclaredFields()) {
                if (field.getName().equals(fieldName)) {
                    return field;
                }
            }
            searchType = searchType.getSuperclass();
        }
        return null;
    }

    private Object findRepositoryForEntity(Class<?> entityType) {
        try {
            Map<String, Object> repositories = applicationContext.getBeansOfType(Object.class);

            for (Object repository : repositories.values()) {
                Class<?> repoClass = repository.getClass();
                Type genericSuperclass = repoClass.getGenericSuperclass();

                if (genericSuperclass instanceof ParameterizedType) {
                    ParameterizedType paramType = (ParameterizedType) genericSuperclass;
                    Type[] typeArgs = paramType.getActualTypeArguments();

                    if (typeArgs.length > 0 && typeArgs[0] instanceof Class) {
                        Class<?> repoEntityType = (Class<?>) typeArgs[0];
                        if (entityType.equals(repoEntityType)) {
                            return repository;
                        }
                    }
                }
            }
            String repositoryBeanName = entityType.getSimpleName() + "Repository";
            repositoryBeanName = repositoryBeanName.substring(0, 1).toLowerCase() + repositoryBeanName.substring(1);

            if (applicationContext.containsBean(repositoryBeanName)) {
                return applicationContext.getBean(repositoryBeanName);
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private List<?> invokeRepositoryFindAll(Object repository) {
        try {
            Method findAllMethod = repository.getClass().getMethod("findAll");
            return (List<?>) findAllMethod.invoke(repository);
        } catch (Exception e) {
            return null;
        }
    }

    private Object handleFilterError(Object filterTarget) {
        if (filterTarget instanceof Collection<?>) {
            return List.of();
        } else if (filterTarget instanceof Page<?>) {
            Page<?> page = (Page<?>) filterTarget;
            return new PageImpl<>(List.of(), page.getPageable(), 0);
        } else if (filterTarget.getClass().isArray()) {
            return new Object[0];
        }
        return null;
    }
}