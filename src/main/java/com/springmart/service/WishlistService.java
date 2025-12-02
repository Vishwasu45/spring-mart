package com.springmart.service;

import com.springmart.dto.ProductDTO;
import com.springmart.entity.Product;
import com.springmart.entity.User;
import com.springmart.entity.WishlistItem;
import com.springmart.exception.ResourceNotFoundException;
import com.springmart.repository.ProductRepository;
import com.springmart.repository.UserRepository;
import com.springmart.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public void addToWishlist(Long productId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // Check if already in wishlist
        if (wishlistRepository.existsByUserIdAndProductId(user.getId(), productId)) {
            log.info("Product {} already in wishlist for user {}", productId, userEmail);
            return; // Already exists, silently return
        }

        WishlistItem wishlistItem = WishlistItem.builder()
                .user(user)
                .product(product)
                .build();

        wishlistRepository.save(wishlistItem);
        log.info("Added product {} to wishlist for user {}", productId, userEmail);
    }

    public void removeFromWishlist(Long productId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        wishlistRepository.deleteByUserIdAndProductId(user.getId(), productId);
        log.info("Removed product {} from wishlist for user {}", productId, userEmail);
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> getWishlist(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<WishlistItem> wishlistItems = wishlistRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        
        return wishlistItems.stream()
                .map(item -> mapProductToDTO(item.getProduct()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean isInWishlist(Long productId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElse(null);
        
        if (user == null) {
            return false;
        }

        return wishlistRepository.existsByUserIdAndProductId(user.getId(), productId);
    }

    @Transactional(readOnly = true)
    public Long getWishlistCount(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElse(null);
        
        if (user == null) {
            return 0L;
        }

        return wishlistRepository.countByUserId(user.getId());
    }

    private ProductDTO mapProductToDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .discountPercentage(product.getDiscountPercentage())
                .stockQuantity(product.getStockQuantity())
                .sku(product.getSku())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .sellerName(product.getSeller().getName())
                .primaryImageUrl(product.getPrimaryImageUrl())
                .isFeatured(product.getIsFeatured())
                .averageRating(product.getAverageRating())
                .reviewCount(product.getReviews() != null ? product.getReviews().size() : 0)
                .build();
    }
}
