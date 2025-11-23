package com.springmart.service;

import com.springmart.dto.CartItemDTO;
import com.springmart.entity.CartItem;
import com.springmart.entity.Product;
import com.springmart.entity.User;
import com.springmart.exception.InsufficientStockException;
import com.springmart.exception.ResourceNotFoundException;
import com.springmart.repository.CartItemRepository;
import com.springmart.repository.ProductRepository;
import com.springmart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public List<CartItemDTO> getCartItems(Long userId) {
        return cartItemRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public BigDecimal getCartTotal(Long userId) {
        return getCartItems(userId).stream()
                .map(CartItemDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Integer getCartItemCount(Long userId) {
        return cartItemRepository.findByUserId(userId).stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    @Transactional
    public CartItemDTO addToCart(Long userId, Long productId, Integer quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        if (product.getStockQuantity() < quantity) {
            throw new InsufficientStockException(product.getName(), quantity, product.getStockQuantity());
        }

        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, productId)
                .map(existing -> {
                    int newQuantity = existing.getQuantity() + quantity;
                    if (product.getStockQuantity() < newQuantity) {
                        throw new InsufficientStockException(product.getName(), newQuantity,
                                product.getStockQuantity());
                    }
                    existing.setQuantity(newQuantity);
                    return existing;
                })
                .orElseGet(() -> CartItem.builder()
                        .user(user)
                        .product(product)
                        .quantity(quantity)
                        .build());

        CartItem savedItem = cartItemRepository.save(cartItem);
        log.info("Added product {} to cart for user {}", productId, userId);

        return convertToDTO(savedItem);
    }

    @Transactional
    public CartItemDTO updateCartItem(Long cartItemId, Integer quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));

        if (cartItem.getProduct().getStockQuantity() < quantity) {
            throw new InsufficientStockException(
                    cartItem.getProduct().getName(),
                    quantity,
                    cartItem.getProduct().getStockQuantity());
        }

        cartItem.setQuantity(quantity);
        CartItem updatedItem = cartItemRepository.save(cartItem);

        return convertToDTO(updatedItem);
    }

    @Transactional
    public void removeFromCart(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));

        cartItemRepository.delete(cartItem);
        log.info("Removed cart item {}", cartItemId);
    }

    @Transactional
    public void clearCart(Long userId) {
        cartItemRepository.deleteAllByUserId(userId);
        log.info("Cleared cart for user {}", userId);
    }

    private CartItemDTO convertToDTO(CartItem cartItem) {
        BigDecimal subtotal = cartItem.getProduct().getPrice()
                .multiply(BigDecimal.valueOf(cartItem.getQuantity()));

        String imageUrl = cartItem.getProduct().getImages().stream()
                .filter(img -> img.getIsPrimary())
                .findFirst()
                .map(img -> img.getImageUrl())
                .orElse(null);

        return CartItemDTO.builder()
                .id(cartItem.getId())
                .productId(cartItem.getProduct().getId())
                .productName(cartItem.getProduct().getName())
                .productPrice(cartItem.getProduct().getPrice())
                .productImageUrl(imageUrl)
                .quantity(cartItem.getQuantity())
                .subtotal(subtotal)
                .build();
    }
}
