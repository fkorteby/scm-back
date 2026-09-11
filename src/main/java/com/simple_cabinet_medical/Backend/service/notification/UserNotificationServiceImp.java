package com.simple_cabinet_medical.Backend.service.notification;

import com.simple_cabinet_medical.Backend.Dto.notifications.UserNotificationResponseDto;
import com.simple_cabinet_medical.Backend.model.notifications.ENotificationStatus;
import com.simple_cabinet_medical.Backend.model.notifications.Notification;
import com.simple_cabinet_medical.Backend.model.notifications.NotificationTarget;
import com.simple_cabinet_medical.Backend.repository.notification.NotificationRepository;
import com.simple_cabinet_medical.Backend.repository.notification.NotificationTargetRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserNotificationServiceImp implements UserNotificationService {

    private static final ENotificationStatus NOTIFICATION_STATUS = ENotificationStatus.ACTIVE;

    private final NotificationRepository notificationRepository;
    private final NotificationTargetRepository notificationTargetRepository;


    @Override
    public Page<UserNotificationResponseDto> getMyNotifications(Long userId, Pageable pageable) {

        Page<Notification> myNotifications =
                notificationRepository.findMyNotifications(
                        userId,
                        NOTIFICATION_STATUS,
                        pageable
                );

        return myNotifications.map(notification -> {

            NotificationTarget target = notification.getNotificationTarget()
                    .stream()
                    .filter(t -> t.getUserId().equals(userId))
                    .findFirst()
                    .orElse(null);

            return new UserNotificationResponseDto(
                    notification.getId(),
                    notification.getTitle(),
                    notification.getDescription(),
                    notification.getLink(),
                    target != null && target.isRead(),
                    target != null && target.isPushSent()
            );
        });
    }

    @Override
    @Transactional
    public Boolean markAsRead(Long userId, Long notificationId) {

        Optional<NotificationTarget> existingTarget = notificationTargetRepository
                .findNotificationTargetByUserIdAndNotificationId(userId, notificationId);

        if (existingTarget.isPresent()) {
            NotificationTarget target = existingTarget.get();
            if (!target.isRead()) {
                target.setRead(true);
                target.setReadAt(LocalDateTime.now());
                notificationTargetRepository.save(target);
            }
            return true;
        }

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Notification introuvable, id=" + notificationId));
        if (notification.isGlobal()) {
            NotificationTarget newTarget = new NotificationTarget();
            newTarget.setUserId(userId);
            newTarget.setRead(true);
            newTarget.setReadAt(LocalDateTime.now());
            newTarget.setPushSent(false);

            notification.addTarget(newTarget);

            try {
                notificationRepository.save(notification);
            } catch (DataIntegrityViolationException e) {
                notificationTargetRepository
                        .findNotificationTargetByUserIdAndNotificationId(userId, notificationId)
                        .ifPresent(t -> {
                            if (!t.isRead()) {
                                t.setRead(true);
                                t.setReadAt(LocalDateTime.now());
                                notificationTargetRepository.save(t);
                            }
                        });
            }

            return true;
        }

        throw new EntityNotFoundException(
                "Aucune cible trouvée pour userId=" + userId + " et notificationId=" + notificationId);
    }

    @Override
    @Transactional
    public Boolean markAsPush(Long userId, Long notificationId) {

        Optional<NotificationTarget> existingTarget = notificationTargetRepository
                .findNotificationTargetByUserIdAndNotificationId(userId, notificationId);

        if (existingTarget.isPresent()) {
            NotificationTarget target = existingTarget.get();
            if (!target.isPushSent()) {
                target.setPushSent(true);
                target.setPushSentAt(LocalDateTime.now());
                notificationTargetRepository.save(target);
            }
            return true;
        }

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Notification introuvable, id=" + notificationId));
        if (notification.isGlobal()) {
            NotificationTarget newTarget = new NotificationTarget();
            newTarget.setUserId(userId);
            newTarget.setRead(false);
            newTarget.setReadAt(LocalDateTime.now());
            newTarget.setPushSent(true);

            notification.addTarget(newTarget);

            try {
                notificationRepository.save(notification);
            } catch (DataIntegrityViolationException e) {
                notificationTargetRepository
                        .findNotificationTargetByUserIdAndNotificationId(userId, notificationId)
                        .ifPresent(t -> {
                            if (!t.isPushSent()) {
                                t.setPushSent(true);
                                t.setPushSentAt(LocalDateTime.now());
                                notificationTargetRepository.save(t);
                            }
                        });
            }

            return true;
        }

        throw new EntityNotFoundException(
                "Aucune cible trouvée pour userId=" + userId + " et notificationId=" + notificationId);
    }

}
