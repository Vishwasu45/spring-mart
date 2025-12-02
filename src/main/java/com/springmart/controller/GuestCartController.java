package com.springmart.controller;

import com.springmart.dto.AddToCartRequest;
import com.springmart.dto.CartItemDTO;
import com.springmart.service.GuestCartService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/guest/cart")
@RequiredArgsConstructor
public class GuestCartController {

    private final GuestCartService guestCartService;
    private static final String GUEST_SESSION_COOKIE = "guest_session_id";
    private static final int COOKIE_MAX_AGE = 24 * 60 * 60; // 24 hours

    @GetMapping
    public ResponseEntity<List<CartItemDTO>> getGuestCart(
            @CookieValue(value = GUEST_SESSION_COOKIE, required = false) String sessionId,
            HttpServletResponse response) {
        
        if (sessionId == null) {
            sessionId = guestCartService.generateGuestSessionId();
            addSessionCookie(response, sessionId);
        }
        
        List<CartItemDTO> cart = guestCartService.getGuestCart(sessionId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping
    public ResponseEntity<CartItemDTO> addToCart(
            @RequestBody AddToCartRequest request,
            @CookieValue(value = GUEST_SESSION_COOKIE, required = false) String sessionId,
            HttpServletResponse response) {
        
        if (sessionId == null) {
            sessionId = guestCartService.generateGuestSessionId();
            addSessionCookie(response, sessionId);
        }
        
        CartItemDTO cartItem = guestCartService.addToGuestCart(
                sessionId,
                request.getProductId(),
                request.getQuantity());
        
        return ResponseEntity.ok(cartItem);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<CartItemDTO> updateCartItem(
            @PathVariable Long productId,
            @RequestParam Integer quantity,
            @CookieValue(value = GUEST_SESSION_COOKIE) String sessionId) {
        
        CartItemDTO cartItem = guestCartService.updateGuestCartItem(sessionId, productId, quantity);
        return ResponseEntity.ok(cartItem);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeFromCart(
            @PathVariable Long productId,
            @CookieValue(value = GUEST_SESSION_COOKIE) String sessionId) {
        
        guestCartService.removeFromGuestCart(sessionId, productId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(
            @CookieValue(value = GUEST_SESSION_COOKIE) String sessionId) {
        
        guestCartService.clearGuestCart(sessionId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/total")
    public ResponseEntity<BigDecimal> getCartTotal(
            @CookieValue(value = GUEST_SESSION_COOKIE, required = false) String sessionId) {
        
        if (sessionId == null) {
            return ResponseEntity.ok(BigDecimal.ZERO);
        }
        
        BigDecimal total = guestCartService.getGuestCartTotal(sessionId);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getCartItemCount(
            @CookieValue(value = GUEST_SESSION_COOKIE, required = false) String sessionId) {
        
        if (sessionId == null) {
            return ResponseEntity.ok(0L);
        }
        
        long count = guestCartService.getGuestCartItemCount(sessionId);
        return ResponseEntity.ok(count);
    }

    private void addSessionCookie(HttpServletResponse response, String sessionId) {
        Cookie cookie = new Cookie(GUEST_SESSION_COOKIE, sessionId);
        cookie.setMaxAge(COOKIE_MAX_AGE);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }
}
