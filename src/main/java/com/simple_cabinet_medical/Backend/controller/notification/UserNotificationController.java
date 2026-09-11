package com.simple_cabinet_medical.Backend.controller.notification;

import com.simple_cabinet_medical.Backend.Dto.notifications.UserNotificationResponseDto;
import com.simple_cabinet_medical.Backend.model.Utilisateur;
import com.simple_cabinet_medical.Backend.service.notification.UserNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class UserNotificationController {
    private final UserNotificationService userNotificationService;

    @GetMapping("/myNotifications")
    public ResponseEntity<Page<UserNotificationResponseDto>> myNotifications(
            Pageable pageable,
            @AuthenticationPrincipal Utilisateur user
    ) {
        Page<UserNotificationResponseDto> myNotifications =
                userNotificationService.getMyNotifications(user.getIdUtilisateur(), pageable);
        return new ResponseEntity<>(myNotifications, HttpStatus.OK);
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Boolean> markAsResd(
            @AuthenticationPrincipal Utilisateur user,
            @PathVariable Long notificationId){
        return new ResponseEntity<>(userNotificationService
                .markAsRead(user.getIdUtilisateur(), notificationId),HttpStatus.OK);
    }

    @PatchMapping("/{notificationId}/pushed")
    public ResponseEntity<Boolean> markAsPush(
            @AuthenticationPrincipal Utilisateur user,
            @PathVariable Long notificationId){
        return new ResponseEntity<>(userNotificationService
                .markAsPush(user.getIdUtilisateur(), notificationId),HttpStatus.OK);
    }
}
