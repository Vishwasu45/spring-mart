package com.springmart.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductFilterRequest {
    
    private List<Long> categoryIds;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer minRating;  // 1-5
    private Boolean inStock;
    private Boolean onSale;     // discountPercentage > 0
    private Boolean featured;
    private String sortBy;      // price_asc, price_desc, rating_desc, newest, name_asc
    
    // For pagination
    @Builder.Default
    private Integer page = 0;
    @Builder.Default
    private Integer size = 12;
}
