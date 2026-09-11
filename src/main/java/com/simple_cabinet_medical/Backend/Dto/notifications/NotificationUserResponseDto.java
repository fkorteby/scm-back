package com.simple_cabinet_medical.Backend.Dto.notifications;

import java.time.LocalDateTime;

public record NotificationUserResponseDto(
        Long userId,
        String username,
        String email,
        Long clientId,
        String clientName,
        boolean isRead,
        LocalDateTime readAt,
        boolean pushSent,
        LocalDateTime pushSentAt
) {
}