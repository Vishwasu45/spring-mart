package com.springmart.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {

    private Long id;

    @NotBlank(message = "Product name is required")
    @Size(min = 3, max = 255, message = "Product name must be between 3 and 255 characters")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    @NotNull(message = "Category is required")
    private Long categoryId;

    private String categoryName;

    private Long sellerId;
    private String sellerName;

    private String sku;
    private String slug;
    private Boolean isActive;
    private Boolean isFeatured;
    private Integer discountPercentage;

    private List<String> imageUrls;
    private String primaryImageUrl;

    private Double averageRating;
    private Integer reviewCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
