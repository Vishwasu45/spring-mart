package com.springmart.service;

import com.springmart.dto.CartItemDTO;
import com.springmart.entity.GuestCartItem;
import com.springmart.entity.GuestSession;
import com.springmart.entity.Product;
import com.springmart.exception.InsufficientStockException;
import com.springmart.exception.ResourceNotFoundException;
import com.springmart.repository.GuestCartItemRepository;
import com.springmart.repository.GuestSessionRepository;
import com.springmart.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GuestCartService {

    private final GuestCartItemRepository guestCartItemRepository;
    private final GuestSessionRepository guestSessionRepository;
    private final ProductRepository productRepository;

    private static final int SESSION_DURATION_HOURS = 24;

    /**
     * Generate a new guest session ID
     */
    public String generateGuestSessionId() {
        String sessionId = UUID.randomUUID().toString();
        
        GuestSession session = GuestSession.builder()
                .sessionId(sessionId)
                .expiresAt(LocalDateTime.now().plusHours(SESSION_DURATION_HOURS))
                .build();
        
        guestSessionRepository.save(session);
        log.info("Created new guest session: {}", sessionId);
        
        return sessionId;
    }

    /**
     * Get all cart items for a guest session
     */
    public List<CartItemDTO> getGuestCart(String sessionId) {
        validateSession(sessionId);
        
        List<GuestCartItem> cartItems = guestCartItemRepository.findBySessionId(sessionId);
        return cartItems.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Add item to guest cart
     */
    @Transactional
    public CartItemDTO addToGuestCart(String sessionId, Long productId, Integer quantity) {
        validateSession(sessionId);
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        if (product.getStockQuantity() < quantity) {
            throw new InsufficientStockException(
                    product.getName(), 
                    quantity, 
                    product.getStockQuantity());
        }

        GuestCartItem cartItem = guestCartItemRepository
                .findBySessionIdAndProductId(sessionId, productId)
                .map(existing -> {
                    int newQuantity = existing.getQuantity() + quantity;
                    if (product.getStockQuantity() < newQuantity) {
                        throw new InsufficientStockException(
                                product.getName(), 
                                newQuantity, 
                                product.getStockQuantity());
                    }
                    existing.setQuantity(newQuantity);
                    return existing;
                })
                .orElse(GuestCartItem.builder()
                        .sessionId(sessionId)
                        .product(product)
                        .quantity(quantity)
                        .build());

        GuestCartItem savedItem = guestCartItemRepository.save(cartItem);
        log.info("Added product {} to guest cart {}", productId, sessionId);

        return convertToDTO(savedItem);
    }

    /**
     * Update cart item quantity
     */
    @Transactional
    public CartItemDTO updateGuestCartItem(String sessionId, Long productId, Integer quantity) {
        validateSession(sessionId);
        
        GuestCartItem cartItem = guestCartItemRepository
                .findBySessionIdAndProductId(sessionId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item", "productId", productId));

        Product product = cartItem.getProduct();
        if (product.getStockQuantity() < quantity) {
            throw new InsufficientStockException(
                    product.getName(), 
                    quantity, 
                    product.getStockQuantity());
        }

        cartItem.setQuantity(quantity);
        GuestCartItem savedItem = guestCartItemRepository.save(cartItem);
        
        return convertToDTO(savedItem);
    }

    /**
     * Remove item from guest cart
     */
    @Transactional
    public void removeFromGuestCart(String sessionId, Long productId) {
        validateSession(sessionId);
        
        GuestCartItem cartItem = guestCartItemRepository
                .findBySessionIdAndProductId(sessionId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item", "productId", productId));

        guestCartItemRepository.delete(cartItem);
        log.info("Removed product {} from guest cart {}", productId, sessionId);
    }

    /**
     * Clear guest cart
     */
    @Transactional
    public void clearGuestCart(String sessionId) {
        guestCartItemRepository.deleteBySessionId(sessionId);
        log.info("Cleared guest cart: {}", sessionId);
    }

    /**
     * Get cart total
     */
    public BigDecimal getGuestCartTotal(String sessionId) {
        validateSession(sessionId);
        
        List<GuestCartItem> cartItems = guestCartItemRepository.findBySessionId(sessionId);
        return cartItems.stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Get cart item count
     */
    public long getGuestCartItemCount(String sessionId) {
        return guestCartItemRepository.countBySessionId(sessionId);
    }

    /**
     * Update guest session email when they provide it during checkout
     */
    @Transactional
    public void updateGuestEmail(String sessionId, String email) {
        GuestSession session = guestSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Guest session", "sessionId", sessionId));
        
        session.setEmail(email);
        guestSessionRepository.save(session);
    }

    /**
     * Validate that a guest session exists and is not expired
     */
    private void validateSession(String sessionId) {
        GuestSession session = guestSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Guest session", "sessionId", sessionId));
        
        if (session.isExpired()) {
            throw new IllegalStateException("Guest session has expired");
        }
    }

    /**
     * Convert GuestCartItem entity to DTO
     */
    private CartItemDTO convertToDTO(GuestCartItem cartItem) {
        Product product = cartItem.getProduct();
        return CartItemDTO.builder()
                .id(cartItem.getId())
                .productId(product.getId())
                .productName(product.getName())
                .productPrice(product.getPrice())
                .quantity(cartItem.getQuantity())
                .productImageUrl(product.getPrimaryImageUrl())
                .subtotal(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                .build();
    }

    /**
     * Cleanup expired guest sessions (scheduled task)
     */
    @Transactional
    public void cleanupExpiredSessions() {
        List<GuestSession> expiredSessions = guestSessionRepository
                .findByExpiresAtBefore(LocalDateTime.now());
        
        for (GuestSession session : expiredSessions) {
            guestCartItemRepository.deleteBySessionId(session.getSessionId());
            guestSessionRepository.delete(session);
        }
        
        if (!expiredSessions.isEmpty()) {
            log.info("Cleaned up {} expired guest sessions", expiredSessions.size());
        }
    }
}
