package com.simple_cabinet_medical.Backend.repository.notification;

import com.simple_cabinet_medical.Backend.model.notifications.Notification;
import com.simple_cabinet_medical.Backend.model.notifications.NotificationTarget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationTargetRepository extends JpaRepository<NotificationTarget, Long> {
    Page<NotificationTarget> findAllByNotificationId(Long notificationId, Pageable pageable);

    Optional<NotificationTarget> findNotificationTargetByUserIdAndNotificationId(
            Long userId, Long notificationId);
}
