package com.simple_cabinet_medical.Backend.service.notification;

import com.simple_cabinet_medical.Backend.Dto.notifications.NotificationRequestDto;
import com.simple_cabinet_medical.Backend.Dto.notifications.NotificationResponseDto;
import com.simple_cabinet_medical.Backend.Dto.notifications.NotificationUpdateRequestDto;
import com.simple_cabinet_medical.Backend.Dto.notifications.NotificationUserResponseDto;
import com.simple_cabinet_medical.Backend.model.Utilisateur;
import com.simple_cabinet_medical.Backend.model.notifications.ENotificationType;
import com.simple_cabinet_medical.Backend.model.notifications.Notification;
import com.simple_cabinet_medical.Backend.model.notifications.NotificationTarget;
import com.simple_cabinet_medical.Backend.repository.ClientRepository;
import com.simple_cabinet_medical.Backend.repository.UtilisateurRepository;
import com.simple_cabinet_medical.Backend.repository.notification.NotificationRepository;
import com.simple_cabinet_medical.Backend.repository.notification.NotificationTargetRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImp implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ClientRepository clientRepository;
    private final NotificationTargetRepository notificationTargetRepository;

    @Override
    @Transactional
    public void createNotification(NotificationRequestDto requestDto) {

        log.info("Creating notification with title='{}', global={}",
                requestDto.title(), requestDto.isGlobal());

        Set<Utilisateur> users = new HashSet<>();

        if (!requestDto.isGlobal()) {
            if (requestDto.userIds() != null && !requestDto.userIds().isEmpty()) {
                users.addAll(utilisateurRepository.findAllById(requestDto.userIds()));
            }
            if (requestDto.clientIds() != null && !requestDto.clientIds().isEmpty()) {
                Set<Utilisateur> clientUsers = clientRepository
                        .findAllById(requestDto.clientIds())
                        .stream()
                        .flatMap(client -> client.getUtilisateurs().stream())
                        .collect(Collectors.toSet());
                users.addAll(clientUsers);
            }
            log.info("Resolved {} unique users for targeted notification", users.size());
        }

        Notification notification = new Notification(
                null,
                requestDto.title(),
                requestDto.description(),
                requestDto.startDate(),
                requestDto.endDate(),
                requestDto.link(),
                new HashSet<>(),
                requestDto.status(),
                ENotificationType.PUSH,
                requestDto.isGlobal(),
                null,
                null,
                null
        );

        for (Utilisateur user : users) {
            NotificationTarget target = new NotificationTarget();
            target.setUserId(user.getIdUtilisateur());
            target.setRead(false);
            target.setPushSent(false);
            notification.addTarget(target);
        }

        notificationRepository.save(notification);

//        // Push immédiat uniquement si la notification est déjà visible à cet instant
//        if (notificationBroadcastService.isCurrentlyVisible(
//                notification.getStatus(), notification.getStartDate().atStartOfDay(), notification.getEndDate().atStartOfDay())) {
//
//            Set<Long> targetUserIds = users.stream()
//                    .map(Utilisateur::getIdUtilisateur)
//                    .collect(Collectors.toSet());
//
//            notificationBroadcastService.broadcast(notification, targetUserIds);
//            log.info("Notification id={} créée et immédiatement diffusée (active dès maintenant)",
//                    notification.getId());
//        } else {
//            log.info("Notification id={} créée, diffusion différée (statut={}, hors fenêtre ou programmée)",
//                    notification.getId(), notification.getStatus());
//        }
    }

    @Override
    public Page<NotificationResponseDto> getAllNotifications(Pageable pageable) {

        Page<Notification> notificationPage = notificationRepository.findAll(pageable);

        return notificationPage.map(notification -> new NotificationResponseDto(
                notification.getId(),
                notification.getTitle(),
                notification.getDescription(),
                notification.getStartDate(),
                notification.getEndDate(),
                notification.getLink(),
                notification.getStatus(),
                notification.getType(),
                notification.isGlobal(),
                notification.getCreatedAt(),
                notification.getUpdatedAt(),
                notification.getNotificationTarget()
                        .stream()
                        .map(NotificationTarget::getUserId)
                        .collect(Collectors.toSet())
        ));
    }

    @Override
    public Page<NotificationUserResponseDto> getNotificationById(Long notificationId, Pageable pageable) {

        Page<NotificationTarget> notificationTargets =
                notificationTargetRepository.findAllByNotificationId(notificationId, pageable);

        return notificationTargets.map(target -> {

            Utilisateur user = utilisateurRepository.findById(target.getUserId()).orElse(null);

            if (user == null) {
                log.warn("User not found for notification target, userId={}", target.getUserId());
                return new NotificationUserResponseDto(
                        target.getUserId(), null, null, null, null,
                        target.isRead(), target.getReadAt(),
                        target.isPushSent(), target.getPushSentAt()
                );
            }

            return new NotificationUserResponseDto(
                    user.getIdUtilisateur(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getClient().getIdClient(),
                    user.getClient().getNomClient(),
                    target.isRead(),
                    target.getReadAt(),
                    target.isPushSent(),
                    target.getPushSentAt()
            );
        });
    }

    @Override
    @Transactional
    public void updateNotification(Long notificationId, NotificationUpdateRequestDto requestDto) {

        log.info("Updating notification with id={}", notificationId);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Notification not found, id=" + notificationId));

        notification.setTitle(requestDto.title());
        notification.setDescription(requestDto.description());
        notification.setStartDate(requestDto.startDate());
        notification.setEndDate(requestDto.endDate());
        notification.setLink(requestDto.link());
        notification.setStatus(requestDto.status());
        notification.setGlobal(requestDto.isGlobal());

        Set<Long> currentTargetUserIds;

        if (requestDto.isGlobal()) {
            int removedCount = notification.getNotificationTarget().size();
            removeAllTargets(notification);
            currentTargetUserIds = Set.of();
            log.info("Notification id={} set to global — {} target(s) removed",
                    notificationId, removedCount);
        } else {
            syncNotificationTargets(notification, safeSet(requestDto.userIds()));
            currentTargetUserIds = notification.getNotificationTarget()
                    .stream()
                    .map(NotificationTarget::getUserId)
                    .collect(Collectors.toSet());
        }

        notificationRepository.save(notification);

//        // Repush si la notification est (re)devenue visible suite à la modification.
//        // ⚠️ Ceci pousse une notification à CHAQUE édition tant qu'elle reste active
//        // et dans sa fenêtre de dates — pas seulement lors du premier passage à "active".
//        // Si vous préférez ne pousser qu'au moment où elle DEVIENT active (transition),
//        // il faudrait comparer l'ancien statut avant modification. Dites-le-moi si
//        // c'est le comportement souhaité, je l'ajuste.
//        if (notificationBroadcastService.isCurrentlyVisible(
//                notification.getStatus(), notification.getStartDate().atStartOfDay(), notification.getEndDate().atStartOfDay())) {
//
//            notificationBroadcastService.broadcast(notification, currentTargetUserIds);
//            log.info("Notification id={} mise à jour et re-diffusée (visible actuellement)", notificationId);
//        }

        log.info("Notification id={} updated successfully", notificationId);
    }

    @Override
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    private Set<Long> safeSet(Set<Long> ids) {
        return ids != null ? ids : Set.of();
    }

    private void warnIfMissingUsers(Set<Long> requestedIds, List<Utilisateur> foundUsers) {
        if (foundUsers.size() == requestedIds.size()) {
            return;
        }
        Set<Long> foundIds = foundUsers.stream()
                .map(Utilisateur::getIdUtilisateur)
                .collect(Collectors.toSet());
        Set<Long> missing = new HashSet<>(requestedIds);
        missing.removeAll(foundIds);
        log.warn("{} userId(s) not found and ignored: {}", missing.size(), missing);
    }

    private void syncNotificationTargets(Notification notification, Set<Long> newUserIds) {

        Map<Long, NotificationTarget> existingByUserId = notification.getNotificationTarget()
                .stream()
                .collect(Collectors.toMap(NotificationTarget::getUserId, t -> t));

        Set<Long> currentUserIds = existingByUserId.keySet();

        Set<Long> userIdsToRemove = new HashSet<>(currentUserIds);
        userIdsToRemove.removeAll(newUserIds);

        Set<Long> userIdsToAdd = new HashSet<>(newUserIds);
        userIdsToAdd.removeAll(currentUserIds);

        if (!userIdsToRemove.isEmpty()) {
            List<NotificationTarget> targetsToRemove = userIdsToRemove.stream()
                    .map(existingByUserId::get)
                    .collect(Collectors.toList());

            notification.getNotificationTarget().removeAll(targetsToRemove);
            notificationTargetRepository.deleteAll(targetsToRemove);

            log.info("Notification id={} — {} user(s) removed: {}",
                    notification.getId(), targetsToRemove.size(), userIdsToRemove);
        }

        if (!userIdsToAdd.isEmpty()) {
            List<Utilisateur> usersToAdd = utilisateurRepository.findAllById(userIdsToAdd);
            warnIfMissingUsers(userIdsToAdd, usersToAdd);

            for (Utilisateur user : usersToAdd) {
                NotificationTarget target = new NotificationTarget();
                target.setUserId(user.getIdUtilisateur());
                target.setRead(false);
                target.setPushSent(false);
                notification.addTarget(target);
            }

            log.info("Notification id={} — {} new user(s) added",
                    notification.getId(), usersToAdd.size());
        }

        if (userIdsToRemove.isEmpty() && userIdsToAdd.isEmpty()) {
            log.info("Notification id={} — no changes to the recipient list", notification.getId());
        }
    }

    private void removeAllTargets(Notification notification) {
        Set<NotificationTarget> current = new HashSet<>(notification.getNotificationTarget());
        notification.getNotificationTarget().clear();

        if (!current.isEmpty()) {
            notificationTargetRepository.deleteAll(current);
        }
    }
}