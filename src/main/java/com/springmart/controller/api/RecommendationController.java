package com.springmart.controller.api;

import com.springmart.dto.ProductDTO;
import com.springmart.security.CustomOAuth2User;
import com.springmart.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
@Tag(name = "Recommendations", description = "Product recommendation APIs")
public class RecommendationController {

    private final ProductService productService;

    @GetMapping("/similar/{productId}")
    @Operation(summary = "Get similar products")
    public ResponseEntity<List<ProductDTO>> getSimilarProducts(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "6") int limit) {
        return ResponseEntity.ok(productService.getSimilarProducts(productId, limit));
    }

    @GetMapping("/personalized")
    @Operation(summary = "Get personalized recommendations for the current user")
    public ResponseEntity<List<ProductDTO>> getPersonalizedRecommendations(
            @AuthenticationPrincipal CustomOAuth2User currentUser,
            @RequestParam(defaultValue = "8") int limit) {
        return ResponseEntity.ok(productService.getPersonalizedRecommendations(currentUser.getId(), limit));
    }

    @GetMapping("/trending")
    @Operation(summary = "Get trending products")
    public ResponseEntity<List<ProductDTO>> getTrendingProducts(
            @RequestParam(defaultValue = "6") int limit) {
        return ResponseEntity.ok(productService.getTrendingProducts(limit));
    }
}
