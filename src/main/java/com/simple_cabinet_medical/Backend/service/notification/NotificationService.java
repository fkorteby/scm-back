package com.simple_cabinet_medical.Backend.service.notification;

import com.simple_cabinet_medical.Backend.Dto.notifications.NotificationRequestDto;
import com.simple_cabinet_medical.Backend.Dto.notifications.NotificationResponseDto;
import com.simple_cabinet_medical.Backend.Dto.notifications.NotificationUpdateRequestDto;
import com.simple_cabinet_medical.Backend.Dto.notifications.NotificationUserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    void createNotification(NotificationRequestDto requestDto);

    Page<NotificationResponseDto> getAllNotifications(Pageable pageable);

    Page<NotificationUserResponseDto> getNotificationById(Long notificationId, Pageable pageable);

    void updateNotification(Long notificationId, NotificationUpdateRequestDto requestDto);

    void deleteNotification(Long notificationId);
}
