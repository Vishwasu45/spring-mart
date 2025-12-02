package com.springmart.controller;

import com.springmart.entity.User;
import com.springmart.repository.UserRepository;
import com.springmart.security.CustomOAuth2User;
import com.springmart.service.SNSService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for managing user notification preferences.
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationPreferencesController {

    private final UserRepository userRepository;
    private final SNSService snsService;

    /**
     * Get current user's notification preferences.
     *
     * @param oauth2User the authenticated user
     * @return notification preferences
     */
    @GetMapping("/preferences")
    public ResponseEntity<Map<String, Object>> getPreferences(
            @AuthenticationPrincipal CustomOAuth2User oauth2User) {

        if (oauth2User == null) {
            return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
        }

        User user = oauth2User.getUser();

        Map<String, Object> preferences = new HashMap<>();
        preferences.put("emailNotificationsEnabled", user.getEmailNotificationsEnabled());
        preferences.put("emailSubscriptionConfirmed", user.getEmailSubscriptionConfirmed());
        preferences.put("emailSubscribedAt", user.getEmailSubscribedAt());
        preferences.put("hasSubscriptionArn", user.getSnsSubscriptionArn() != null);

        return ResponseEntity.ok(preferences);
    }

    /**
     * Enable email notifications for the current user.
     *
     * @param oauth2User the authenticated user
     * @return response with updated preferences
     */
    @PostMapping("/enable")
    public ResponseEntity<Map<String, Object>> enableNotifications(
            @AuthenticationPrincipal CustomOAuth2User oauth2User) {

        if (oauth2User == null) {
            return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
        }

        User user = oauth2User.getUser();

        try {
            if (user.getSnsSubscriptionArn() == null) {
                // Subscribe to SNS
                String subscriptionArn = snsService.subscribeEmail(user.getEmail());
                user.setSnsSubscriptionArn(subscriptionArn);
                user.setEmailSubscribedAt(LocalDateTime.now());
            }

            user.setEmailNotificationsEnabled(true);
            userRepository.save(user);

            log.info("Email notifications enabled for user: {}", user.getEmail());

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Email notifications enabled. Please check your email to confirm subscription.",
                "emailNotificationsEnabled", true
            ));

        } catch (Exception e) {
            log.error("Failed to enable notifications for user: {}", user.getEmail(), e);
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "error", "Failed to enable email notifications: " + e.getMessage()
            ));
        }
    }

    /**
     * Disable email notifications for the current user.
     *
     * @param oauth2User the authenticated user
     * @return response with updated preferences
     */
    @PostMapping("/disable")
    public ResponseEntity<Map<String, Object>> disableNotifications(
            @AuthenticationPrincipal CustomOAuth2User oauth2User) {

        if (oauth2User == null) {
            return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
        }

        User user = oauth2User.getUser();

        try {
            if (user.getSnsSubscriptionArn() != null) {
                // Unsubscribe from SNS
                snsService.unsubscribeEmail(user.getSnsSubscriptionArn());
                user.setSnsSubscriptionArn(null);
                user.setEmailSubscriptionConfirmed(false);
            }

            user.setEmailNotificationsEnabled(false);
            userRepository.save(user);

            log.info("Email notifications disabled for user: {}", user.getEmail());

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Email notifications disabled successfully.",
                "emailNotificationsEnabled", false
            ));

        } catch (Exception e) {
            log.error("Failed to disable notifications for user: {}", user.getEmail(), e);
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "error", "Failed to disable email notifications: " + e.getMessage()
            ));
        }
    }

    /**
     * Check subscription confirmation status.
     *
     * @param oauth2User the authenticated user
     * @return subscription status
     */
    @GetMapping("/check-confirmation")
    public ResponseEntity<Map<String, Object>> checkConfirmation(
            @AuthenticationPrincipal CustomOAuth2User oauth2User) {

        if (oauth2User == null) {
            return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
        }

        User user = oauth2User.getUser();

        if (user.getSnsSubscriptionArn() == null) {
            return ResponseEntity.ok(Map.of(
                "confirmed", false,
                "message", "Not subscribed to email notifications"
            ));
        }

        try {
            boolean confirmed = snsService.isSubscriptionConfirmed(user.getSnsSubscriptionArn());

            if (confirmed && !user.getEmailSubscriptionConfirmed()) {
                user.setEmailSubscriptionConfirmed(true);
                userRepository.save(user);
            }

            return ResponseEntity.ok(Map.of(
                "confirmed", confirmed,
                "message", confirmed ? "Email subscription confirmed" : "Email subscription pending confirmation"
            ));

        } catch (Exception e) {
            log.error("Failed to check confirmation status for user: {}", user.getEmail(), e);
            return ResponseEntity.status(500).body(Map.of(
                "error", "Failed to check confirmation status"
            ));
        }
    }

    /**
     * Resend confirmation email.
     *
     * @param oauth2User the authenticated user
     * @return response
     */
    @PostMapping("/resend-confirmation")
    public ResponseEntity<Map<String, Object>> resendConfirmation(
            @AuthenticationPrincipal CustomOAuth2User oauth2User) {

        if (oauth2User == null) {
            return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
        }

        User user = oauth2User.getUser();

        try {
            // Unsubscribe old subscription if exists
            if (user.getSnsSubscriptionArn() != null) {
                try {
                    snsService.unsubscribeEmail(user.getSnsSubscriptionArn());
                } catch (Exception e) {
                    log.warn("Failed to unsubscribe old subscription, proceeding anyway", e);
                }
            }

            // Create new subscription
            String subscriptionArn = snsService.subscribeEmail(user.getEmail());
            user.setSnsSubscriptionArn(subscriptionArn);
            user.setEmailSubscribedAt(LocalDateTime.now());
            user.setEmailSubscriptionConfirmed(false);
            userRepository.save(user);

            log.info("Resent confirmation email for user: {}", user.getEmail());

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Confirmation email sent. Please check your inbox."
            ));

        } catch (Exception e) {
            log.error("Failed to resend confirmation for user: {}", user.getEmail(), e);
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "error", "Failed to resend confirmation email"
            ));
        }
    }
}

