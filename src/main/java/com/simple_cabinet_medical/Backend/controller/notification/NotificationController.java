package com.simple_cabinet_medical.Backend.controller.notification;

import com.simple_cabinet_medical.Backend.Dto.notifications.NotificationRequestDto;
import com.simple_cabinet_medical.Backend.Dto.notifications.NotificationResponseDto;
import com.simple_cabinet_medical.Backend.Dto.notifications.NotificationUpdateRequestDto;
import com.simple_cabinet_medical.Backend.Dto.notifications.NotificationUserResponseDto;
import com.simple_cabinet_medical.Backend.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<Void> createNotification(
            @RequestBody NotificationRequestDto requestDto) {

        notificationService.createNotification(requestDto);

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<NotificationResponseDto>> getAllNotifications(
            Pageable pageable) {

        return ResponseEntity.ok(
                notificationService.getAllNotifications(pageable)
        );
    }

    @GetMapping("/{notificationId}")
    public ResponseEntity<Page<NotificationUserResponseDto>> getNotificationById(
            @PathVariable Long notificationId,
            Pageable pageable) {

        return ResponseEntity.ok(
                notificationService.getNotificationById(
                        notificationId,
                        pageable
                )
        );
    }

    @PutMapping("/{notificationId}")
    public ResponseEntity<Void> updateNotification(
            @PathVariable Long notificationId,
            @RequestBody NotificationUpdateRequestDto requestDto) {

        notificationService.updateNotification(
                notificationId,
                requestDto
        );

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable Long notificationId) {

        notificationService.deleteNotification(notificationId);

        return ResponseEntity.noContent().build();
    }
}