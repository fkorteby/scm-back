package com.simple_cabinet_medical.Backend.Dto.notifications;

import com.simple_cabinet_medical.Backend.model.notifications.ENotificationStatus;
import com.simple_cabinet_medical.Backend.model.notifications.ENotificationType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public record NotificationResponseDto(
        Long id,
        String title,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        String link,
        ENotificationStatus status,
        ENotificationType type,
        boolean global,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Set<Long> userIds
) {
}
