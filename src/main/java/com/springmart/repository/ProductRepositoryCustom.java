package com.springmart.repository;

import com.springmart.dto.ProductFilterRequest;
import com.springmart.entity.Product;
import org.springframework.data.domain.Page;

public interface ProductRepositoryCustom {
    Page<Product> findWithFilters(ProductFilterRequest filterRequest);
}
