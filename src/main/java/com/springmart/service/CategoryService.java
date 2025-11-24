package com.springmart.service;

import com.springmart.dto.CategoryDTO;
import com.springmart.entity.Category;
import com.springmart.exception.ResourceNotFoundException;
import com.springmart.repository.CategoryRepository;
import com.springmart.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    // @Cacheable(value = "categories", key = "'all-v2'")
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        return convertToDTO(category);
    }

    public CategoryDTO getCategoryBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "slug", slug));
        return convertToDTO(category);
    }

    private CategoryDTO convertToDTO(Category category) {
        long productCount = productRepository.countByCategoryIdAndIsActiveTrue(category.getId());
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .slug(category.getSlug())
                .imageUrl(category.getImageUrl())
                .productCount(productCount)
                .build();
    }
}
