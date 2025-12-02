package com.springmart.controller.api;

import com.springmart.dto.ProductDTO;
import com.springmart.dto.ProductFilterRequest;
import com.springmart.security.CustomOAuth2User;
import com.springmart.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product management APIs")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Get all active products", description = "Returns a paginated list of all active products")
    public ResponseEntity<Page<ProductDTO>> getAllProducts(
            @PageableDefault(size = 12, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(productService.getAllActiveProducts(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get product by slug")
    public ResponseEntity<ProductDTO> getProductBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(productService.getProductBySlug(slug));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get products by category")
    public ResponseEntity<Page<ProductDTO>> getProductsByCategory(
            @PathVariable Long categoryId,
            @PageableDefault(size = 12) Pageable pageable) {
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId, pageable));
    }

    @GetMapping("/search")
    @Operation(summary = "Search products")
    public ResponseEntity<Page<ProductDTO>> searchProducts(
            @RequestParam String keyword,
            @PageableDefault(size = 12) Pageable pageable) {
        return ResponseEntity.ok(productService.searchProducts(keyword, pageable));
    }

    @GetMapping("/latest")
    @Operation(summary = "Get latest products")
    public ResponseEntity<List<ProductDTO>> getLatestProducts(
            @RequestParam(defaultValue = "8") int limit) {
        return ResponseEntity.ok(productService.getLatestProducts(limit));
    }

    @GetMapping("/top-rated")
    @Operation(summary = "Get top rated products")
    public ResponseEntity<List<ProductDTO>> getTopRatedProducts(
            @RequestParam(defaultValue = "8") int limit) {
        return ResponseEntity.ok(productService.getTopRatedProducts(limit));
    }

    @PostMapping("/filter")
    @Operation(summary = "Filter products", description = "Filter products by various criteria")
    public ResponseEntity<Page<ProductDTO>> filterProducts(@RequestBody ProductFilterRequest filterRequest) {
        return ResponseEntity.ok(productService.getFilteredProducts(filterRequest));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @Operation(summary = "Create a new product", description = "Requires SELLER or ADMIN role")
    public ResponseEntity<ProductDTO> createProduct(
            @Valid @RequestBody ProductDTO productDTO,
            @AuthenticationPrincipal CustomOAuth2User currentUser) {
        ProductDTO created = productService.createProduct(productDTO, currentUser.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @Operation(summary = "Update a product", description = "Requires SELLER or ADMIN role")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductDTO productDTO) {
        return ResponseEntity.ok(productService.updateProduct(id, productDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a product", description = "Requires ADMIN role")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
