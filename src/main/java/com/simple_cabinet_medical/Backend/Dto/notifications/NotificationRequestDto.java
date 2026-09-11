package com.simple_cabinet_medical.Backend.Dto.notifications;

import com.simple_cabinet_medical.Backend.model.notifications.ENotificationStatus;

import java.time.LocalDate;
import java.util.Set;

public record NotificationRequestDto(
        String title,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        String link,
        ENotificationStatus status,
        boolean isGlobal,
        Set<Long> userIds,
        Set<Long> clientIds) {
}