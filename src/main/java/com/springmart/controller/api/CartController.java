package com.springmart.controller.api;

import com.springmart.dto.CartItemDTO;
import com.springmart.security.CustomOAuth2User;
import com.springmart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('USER', 'SELLER', 'ADMIN')")
@Tag(name = "Cart", description = "Shopping cart APIs")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "Get user's cart items")
    public ResponseEntity<List<CartItemDTO>> getCartItems(
            @AuthenticationPrincipal CustomOAuth2User currentUser) {
        return ResponseEntity.ok(cartService.getCartItems(currentUser.getUserId()));
    }

    @GetMapping("/total")
    @Operation(summary = "Get cart total")
    public ResponseEntity<Map<String, BigDecimal>> getCartTotal(
            @AuthenticationPrincipal CustomOAuth2User currentUser) {
        BigDecimal total = cartService.getCartTotal(currentUser.getUserId());
        return ResponseEntity.ok(Map.of("total", total));
    }

    @PostMapping
    @Operation(summary = "Add item to cart")
    public ResponseEntity<CartItemDTO> addToCart(
            @Valid @RequestBody CartItemDTO cartItemDTO,
            @AuthenticationPrincipal CustomOAuth2User currentUser) {
        CartItemDTO added = cartService.addToCart(
                currentUser.getUserId(),
                cartItemDTO.getProductId(),
                cartItemDTO.getQuantity());
        return ResponseEntity.status(HttpStatus.CREATED).body(added);
    }

    @PutMapping("/items/{id}")
    @Operation(summary = "Update cart item quantity")
    public ResponseEntity<CartItemDTO> updateCartItem(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        Integer quantity = request.get("quantity");
        return ResponseEntity.ok(cartService.updateCartItem(id, quantity));
    }

    @DeleteMapping("/items/{id}")
    @Operation(summary = "Remove item from cart")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long id) {
        cartService.removeFromCart(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @Operation(summary = "Clear cart")
    public ResponseEntity<Void> clearCart(
            @AuthenticationPrincipal CustomOAuth2User currentUser) {
        cartService.clearCart(currentUser.getUserId());
        return ResponseEntity.noContent().build();
    }
}
