package com.springmart.service;

import com.springmart.dto.ProductDTO;
import com.springmart.dto.ProductFilterRequest;
import com.springmart.entity.Category;
import com.springmart.entity.Product;
import com.springmart.entity.ProductImage;
import com.springmart.entity.User;
import com.springmart.exception.ResourceNotFoundException;
import com.springmart.repository.CategoryRepository;
import com.springmart.repository.ProductRepository;
import com.springmart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Cacheable(value = "products", key = "#id")
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return convertToDTO(product);
    }

    public ProductDTO getProductBySlug(String slug) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "slug", slug));
        return convertToDTO(product);
    }

    public Page<ProductDTO> getAllActiveProducts(Pageable pageable) {
        return productRepository.findByIsActiveTrue(pageable)
                .map(this::convertToDTO);
    }

    public Page<ProductDTO> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryIdAndIsActiveTrue(categoryId, pageable)
                .map(this::convertToDTO);
    }

    public Page<ProductDTO> searchProducts(String keyword, Pageable pageable) {
        return productRepository.searchProducts(keyword, pageable)
                .map(this::convertToDTO);
    }

    public Page<ProductDTO> getFilteredProducts(ProductFilterRequest filterRequest) {
        return productRepository.findWithFilters(filterRequest)
                .map(this::convertToDTO);
    }

    public List<ProductDTO> getLatestProducts(int limit) {
        return productRepository.findLatestProducts(Pageable.ofSize(limit))
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getTopRatedProducts(int limit) {
        return productRepository.findTopRatedProducts(Pageable.ofSize(limit))
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getFeaturedProducts(int limit) {
        return productRepository.findFeaturedProducts(Pageable.ofSize(limit))
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getFlashDeals(int limit) {
        return productRepository.findProductsOnSale(Pageable.ofSize(limit))
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public ProductDTO createProduct(ProductDTO productDTO, Long sellerId) {
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", productDTO.getCategoryId()));

        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", sellerId));

        Product product = Product.builder()
                .name(productDTO.getName())
                .description(productDTO.getDescription())
                .price(productDTO.getPrice())
                .stockQuantity(productDTO.getStockQuantity())
                .category(category)
                .seller(seller)
                .sku(generateSKU(productDTO.getName()))
                .slug(generateSlug(productDTO.getName()))
                .isActive(true)
                .build();

        Product savedProduct = productRepository.save(product);
        log.info("Created new product: {} (ID: {})", savedProduct.getName(), savedProduct.getId());

        return convertToDTO(savedProduct);
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        if (productDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", productDTO.getCategoryId()));
            product.setCategory(category);
        }

        if (productDTO.getName() != null) {
            product.setName(productDTO.getName());
        }
        if (productDTO.getDescription() != null) {
            product.setDescription(productDTO.getDescription());
        }
        if (productDTO.getPrice() != null) {
            product.setPrice(productDTO.getPrice());
        }
        if (productDTO.getStockQuantity() != null) {
            product.setStockQuantity(productDTO.getStockQuantity());
        }
        if (productDTO.getIsActive() != null) {
            product.setIsActive(productDTO.getIsActive());
        }

        Product updatedProduct = productRepository.save(product);
        log.info("Updated product: {} (ID: {})", updatedProduct.getName(), updatedProduct.getId());

        return convertToDTO(updatedProduct);
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        productRepository.delete(product);
        log.info("Deleted product: {} (ID: {})", product.getName(), id);
    }

    @Transactional
    public void updateStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        product.setStockQuantity(product.getStockQuantity() + quantity);
        productRepository.save(product);
    }

    private ProductDTO convertToDTO(Product product) {
        List<String> imageUrls = product.getImages().stream()
                .map(ProductImage::getImageUrl)
                .collect(Collectors.toList());

        String primaryImageUrl = product.getImages().stream()
                .filter(ProductImage::getIsPrimary)
                .findFirst()
                .map(ProductImage::getImageUrl)
                .orElse(imageUrls.isEmpty() ? null : imageUrls.get(0));

        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .sellerId(product.getSeller().getId())
                .sellerName(product.getSeller().getName())
                .sku(product.getSku())
                .slug(product.getSlug())
                .isActive(product.getIsActive())
                .isFeatured(product.getIsFeatured())
                .discountPercentage(product.getDiscountPercentage())
                .imageUrls(imageUrls)
                .primaryImageUrl(primaryImageUrl)
                .averageRating(product.getAverageRating())
                .reviewCount(product.getReviews().size())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    private String generateSKU(String productName) {
        return "SKU-"
                + productName.replaceAll("[^a-zA-Z0-9]", "").toUpperCase().substring(0,
                        Math.min(10, productName.length()))
                + "-" + System.currentTimeMillis();
    }

    private String generateSlug(String productName) {
        return productName.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                + "-" + System.currentTimeMillis();
    }

    /**
     * Get similar products based on category and price range
     */
    public List<ProductDTO> getSimilarProducts(Long productId, int limit) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        // Calculate price range (±30% of current product price)
        BigDecimal minPrice = product.getPrice().multiply(BigDecimal.valueOf(0.7));
        BigDecimal maxPrice = product.getPrice().multiply(BigDecimal.valueOf(1.3));

        // Find products in same category within price range
        List<Product> similarProducts = productRepository.findByCategoryAndPriceRange(
                product.getCategory().getId(),
                minPrice,
                maxPrice,
                PageRequest.of(0, limit + 1));

        return similarProducts.stream()
                .filter(p -> !p.getId().equals(productId)) // Exclude current product
                .limit(limit)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get personalized recommendations based on user's order history
     */
    public List<ProductDTO> getPersonalizedRecommendations(Long userId, int limit) {
        // Get categories from user's past orders
        List<Long> categoryIds = productRepository.findCategoriesByUserOrders(userId);

        if (categoryIds.isEmpty()) {
            // If no order history, return top rated products
            return getTopRatedProducts(limit);
        }

        // Get top rated products from these categories
        List<Product> recommendedProducts = productRepository.findTopRatedByCategories(
                categoryIds,
                PageRequest.of(0, limit));

        return recommendedProducts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get trending products (most purchased in last 30 days)
     */
    public List<ProductDTO> getTrendingProducts(int limit) {
        List<Product> trendingProducts = productRepository.findTrendingProducts(
                LocalDateTime.now().minusDays(30),
                PageRequest.of(0, limit));

        return trendingProducts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}
