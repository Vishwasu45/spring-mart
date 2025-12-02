package com.springmart.service;

import com.springmart.dto.CreateReviewRequest;
import com.springmart.dto.ReviewDTO;
import com.springmart.entity.Product;
import com.springmart.entity.Review;
import com.springmart.entity.User;
import com.springmart.exception.ResourceNotFoundException;
import com.springmart.repository.ProductRepository;
import com.springmart.repository.ReviewRepository;
import com.springmart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ReviewDTO createReview(CreateReviewRequest request, String userEmail) {
        log.info("Creating review for product {} by user {}", request.getProductId(), userEmail);
        
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // Check if user already reviewed this product
        if (reviewRepository.existsByProductIdAndUserId(product.getId(), user.getId())) {
            throw new IllegalStateException("You have already reviewed this product");
        }

        Review review = Review.builder()
                .product(product)
                .user(user)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        review = reviewRepository.save(review);
        log.info("Review created successfully with ID: {}", review.getId());

        return mapToDTO(review);
    }

    @Transactional(readOnly = true)
    public Page<ReviewDTO> getProductReviews(Long productId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Review> reviews = reviewRepository.findByProductId(productId, pageable);
        return reviews.map(this::mapToDTO);
    }

    @Transactional(readOnly = true)
    public List<ReviewDTO> getProductReviewsList(Long productId) {
        List<Review> reviews = reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
        return reviews.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean hasUserReviewedProduct(Long productId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElse(null);
        if (user == null) {
            return false;
        }
        return reviewRepository.existsByProductIdAndUserId(productId, user.getId());
    }

    @Transactional(readOnly = true)
    public Double getAverageRating(Long productId) {
        Double avg = reviewRepository.getAverageRatingByProductId(productId);
        return avg != null ? avg : 0.0;
    }

    public void deleteReview(Long reviewId, String userEmail) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if the user owns this review
        if (!review.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("You can only delete your own reviews");
        }

        reviewRepository.delete(review);
        log.info("Review {} deleted by user {}", reviewId, userEmail);
    }

    private ReviewDTO mapToDTO(Review review) {
        return ReviewDTO.builder()
                .id(review.getId())
                .productId(review.getProduct().getId())
                .userId(review.getUser().getId())
                .userName(review.getUser().getName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
