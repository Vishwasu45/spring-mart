package com.springmart.controller.api;

import com.springmart.dto.ProductDTO;
import com.springmart.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping("/add/{productId}")
    public ResponseEntity<?> addToWishlist(
            @PathVariable Long productId,
            @AuthenticationPrincipal OAuth2User principal) {
        
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Authentication required"));
        }

        String email = principal.getAttribute("email");
        wishlistService.addToWishlist(productId, email);
        return ResponseEntity.ok(Map.of("message", "Added to wishlist", "inWishlist", true));
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<?> removeFromWishlist(
            @PathVariable Long productId,
            @AuthenticationPrincipal OAuth2User principal) {
        
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Authentication required"));
        }

        String email = principal.getAttribute("email");
        wishlistService.removeFromWishlist(productId, email);
        return ResponseEntity.ok(Map.of("message", "Removed from wishlist", "inWishlist", false));
    }

    @PostMapping("/toggle/{productId}")
    public ResponseEntity<?> toggleWishlist(
            @PathVariable Long productId,
            @AuthenticationPrincipal OAuth2User principal) {
        
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Authentication required"));
        }

        String email = principal.getAttribute("email");
        boolean isInWishlist = wishlistService.isInWishlist(productId, email);
        
        if (isInWishlist) {
            wishlistService.removeFromWishlist(productId, email);
            return ResponseEntity.ok(Map.of("message", "Removed from wishlist", "inWishlist", false));
        } else {
            wishlistService.addToWishlist(productId, email);
            return ResponseEntity.ok(Map.of("message", "Added to wishlist", "inWishlist", true));
        }
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getWishlist(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = principal.getAttribute("email");
        List<ProductDTO> wishlist = wishlistService.getWishlist(email);
        return ResponseEntity.ok(wishlist);
    }

    @GetMapping("/check/{productId}")
    public ResponseEntity<Map<String, Boolean>> checkWishlistStatus(
            @PathVariable Long productId,
            @AuthenticationPrincipal OAuth2User principal) {
        
        if (principal == null) {
            return ResponseEntity.ok(Map.of("inWishlist", false));
        }

        String email = principal.getAttribute("email");
        boolean isInWishlist = wishlistService.isInWishlist(productId, email);
        return ResponseEntity.ok(Map.of("inWishlist", isInWishlist));
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getWishlistCount(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.ok(Map.of("count", 0L));
        }

        String email = principal.getAttribute("email");
        Long count = wishlistService.getWishlistCount(email);
        return ResponseEntity.ok(Map.of("count", count));
    }
}
