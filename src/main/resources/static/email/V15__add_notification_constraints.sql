-- ============================================================
-- NotificationTarget constraints
-- ============================================================

ALTER TABLE notification_target
    ADD CONSTRAINT uk_notification_target_user
        UNIQUE (notification_id, user_id);