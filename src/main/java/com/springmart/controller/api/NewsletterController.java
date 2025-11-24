package com.springmart.controller.api;

import com.springmart.service.NewsletterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/newsletter")
@RequiredArgsConstructor
@Slf4j
public class NewsletterController {

    private final NewsletterService newsletterService;

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");

            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Email is required"));
            }

            // Basic email validation
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Invalid email format"));
            }

            newsletterService.subscribe(email);
            return ResponseEntity.ok(Map.of("success", true, "message", "Successfully subscribed to newsletter!"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            log.error("Error subscribing to newsletter", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "An error occurred. Please try again."));
        }
    }

    @PostMapping("/unsubscribe")
    public ResponseEntity<?> unsubscribe(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            newsletterService.unsubscribe(email);
            return ResponseEntity.ok(Map.of("success", true, "message", "Successfully unsubscribed from newsletter"));
        } catch (Exception e) {
            log.error("Error unsubscribing from newsletter", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "An error occurred. Please try again."));
        }
    }
}

