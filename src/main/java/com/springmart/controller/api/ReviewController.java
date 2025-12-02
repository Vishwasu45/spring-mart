package com.springmart.controller.api;

import com.springmart.dto.CreateReviewRequest;
import com.springmart.dto.ReviewDTO;
import com.springmart.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<?> createReview(
            @Valid @RequestBody CreateReviewRequest request,
            @AuthenticationPrincipal OAuth2User principal) {
        
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Authentication required"));
        }

        String email = principal.getAttribute("email");
        try {
            ReviewDTO review = reviewService.createReview(request, email);
            return ResponseEntity.status(HttpStatus.CREATED).body(review);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<ReviewDTO>> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<ReviewDTO> reviews = reviewService.getProductReviews(productId, page, size);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/product/{productId}/all")
    public ResponseEntity<List<ReviewDTO>> getAllProductReviews(@PathVariable Long productId) {
        List<ReviewDTO> reviews = reviewService.getProductReviewsList(productId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/product/{productId}/check")
    public ResponseEntity<Map<String, Boolean>> checkUserReview(
            @PathVariable Long productId,
            @AuthenticationPrincipal OAuth2User principal) {
        
        if (principal == null) {
            return ResponseEntity.ok(Map.of("hasReviewed", false));
        }

        String email = principal.getAttribute("email");
        boolean hasReviewed = reviewService.hasUserReviewedProduct(productId, email);
        return ResponseEntity.ok(Map.of("hasReviewed", hasReviewed));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal OAuth2User principal) {
        
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Authentication required"));
        }

        String email = principal.getAttribute("email");
        try {
            reviewService.deleteReview(reviewId, email);
            return ResponseEntity.ok(Map.of("message", "Review deleted successfully"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/product/{productId}/average")
    public ResponseEntity<Map<String, Double>> getAverageRating(@PathVariable Long productId) {
        Double avgRating = reviewService.getAverageRating(productId);
        return ResponseEntity.ok(Map.of("averageRating", avgRating));
    }
}
