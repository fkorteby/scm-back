package com.simple_cabinet_medical.Backend.repository.notification;

import com.simple_cabinet_medical.Backend.model.notifications.ENotificationStatus;
import com.simple_cabinet_medical.Backend.model.notifications.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("""
        SELECT DISTINCT n FROM Notification n
        LEFT JOIN n.notificationTarget nt
        WHERE (n.global = true OR nt.userId = :userId)
          AND n.status = :status
          AND (n.startDate IS NULL OR n.startDate <= CURRENT_TIMESTAMP)
          AND (n.endDate IS NULL OR n.endDate >= CURRENT_TIMESTAMP)
        ORDER BY n.createdAt DESC
        """)
    Page<Notification> findMyNotifications(@Param("userId") Long userId,
                                           @Param("status") ENotificationStatus status,
                                           Pageable pageable);

    /**
     * Sélectionne (sans les modifier) les notifications SCHEDULED dont la date de début
     * est arrivée, pour pouvoir les activer ET les diffuser une par une côté service.
     * JOIN FETCH évite le N+1 lors de la lecture des targets pour le broadcast.
     */
    @Query("""
        SELECT DISTINCT n FROM Notification n
        LEFT JOIN FETCH n.notificationTarget
        WHERE n.status = 'SCHEDULED'
          AND n.startDate IS NOT NULL
          AND n.startDate <= :now
          AND (n.endDate IS NULL OR n.endDate >= :now)
        """)
    List<Notification> findScheduledToActivate(@Param("now") LocalDateTime now);

    /**
     * Bascule ACTIVE -> EXPIRED en masse. Pas de broadcast nécessaire pour une expiration,
     * donc un bulk update reste approprié ici (plus performant que charger chaque entité).
     */
    @Modifying
    @Query("""
        UPDATE Notification n
        SET n.status = 'EXPIRED'
        WHERE n.status = 'ACTIVE'
          AND n.endDate IS NOT NULL
          AND n.endDate < :now
        """)
    int expireActiveNotifications(@Param("now") LocalDateTime now);
}