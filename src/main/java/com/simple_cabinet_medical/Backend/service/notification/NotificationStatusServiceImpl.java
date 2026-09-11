package com.simple_cabinet_medical.Backend.service.notification;

import com.simple_cabinet_medical.Backend.model.notifications.ENotificationStatus;
import com.simple_cabinet_medical.Backend.model.notifications.Notification;
import com.simple_cabinet_medical.Backend.model.notifications.NotificationTarget;
import com.simple_cabinet_medical.Backend.repository.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationStatusServiceImpl implements NotificationStatusService {

    private final NotificationRepository notificationRepository;

    @Transactional
    @Override
    public void updateStatuses() {

        LocalDateTime now = LocalDateTime.now();

        int activated = activateAndBroadcastScheduledNotifications(now);
        int expired = notificationRepository.expireActiveNotifications(now);

        log.info("Notification statuses updated: {} activated (and pushed), {} expired",
                activated, expired);
    }

    /**
     * Active chaque notification SCHEDULED dont la date de début est arrivée,
     * puis la diffuse en temps réel — contrairement à l'expiration, l'activation
     * doit déclencher un push puisque c'est le moment où elle devient visible.
     */
    private int activateAndBroadcastScheduledNotifications(LocalDateTime now) {

        List<Notification> toActivate = notificationRepository.findScheduledToActivate(now);

        for (Notification notification : toActivate) {
            notification.setStatus(ENotificationStatus.ACTIVE);
            notificationRepository.save(notification);

//            Set<Long> targetUserIds = notification.getNotificationTarget()
//                    .stream()
//                    .map(NotificationTarget::getUserId)
//                    .collect(Collectors.toSet());

//            notificationBroadcastService.broadcast(notification, targetUserIds);

            log.info("Notification id={} activée par le scheduler ", notification.getId());
        }

        return toActivate.size();
    }
}