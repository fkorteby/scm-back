package com.simple_cabinet_medical.Backend.scheduler;

import com.simple_cabinet_medical.Backend.service.notification.NotificationStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

    private final NotificationStatusService notificationStatusService;

    @Scheduled(
            cron = "${notifications.scheduler.cron}",
            zone = "${notifications.scheduler.zone}"
    )
    public void updateNotificationStatuses() {

        log.info("Starting notification status update");

        notificationStatusService.updateStatuses();

        log.info("Notification status update completed");
    }
}