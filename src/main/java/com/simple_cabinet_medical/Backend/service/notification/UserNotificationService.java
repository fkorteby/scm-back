package com.simple_cabinet_medical.Backend.service.notification;

import com.simple_cabinet_medical.Backend.dto.notifications.UserNotificationResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserNotificationService {

    Page<UserNotificationResponseDto> getMyNotifications(Long userId, Pageable pageable);
    Boolean markAsRead(Long userId, Long notificationId);
    Boolean markAsPush(Long userId, Long notificationId);
}
