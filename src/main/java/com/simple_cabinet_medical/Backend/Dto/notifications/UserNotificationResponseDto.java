package com.simple_cabinet_medical.Backend.Dto.notifications;

public record UserNotificationResponseDto (
        Long id,
        String title,
        String description,
        String link,
        Boolean isRead,
        Boolean isPush){ }
