package com.springmart.service;

import com.springmart.entity.PromoCode;
import com.springmart.entity.PromoCodeUsage;
import com.springmart.entity.User;
import com.springmart.enums.DiscountType;
import com.springmart.exception.ResourceNotFoundException;
import com.springmart.repository.PromoCodeRepository;
import com.springmart.repository.PromoCodeUsageRepository;
import com.springmart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PromoCodeService {

    private final PromoCodeRepository promoCodeRepository;
    private final PromoCodeUsageRepository promoCodeUsageRepository;
    private final UserRepository userRepository;

    public PromoCode validatePromoCode(String code, Long userId, BigDecimal orderAmount) {
        PromoCode promoCode = promoCodeRepository.findByCodeAndIsActiveTrue(code)
                .orElseThrow(() -> new IllegalArgumentException("Invalid promo code"));

        // Check if code is valid (time and usage limits)
        if (!promoCode.isValid()) {
            throw new IllegalArgumentException("This promo code has expired or reached its usage limit");
        }

        // Check minimum purchase amount
        if (promoCode.getMinPurchaseAmount() != null && 
            orderAmount.compareTo(promoCode.getMinPurchaseAmount()) < 0) {
            throw new IllegalArgumentException(
                "Minimum purchase amount of $" + promoCode.getMinPurchaseAmount() + " required for this promo code");
        }

        // Check per-user limit
        long userUsageCount = promoCodeUsageRepository.countByPromoCodeIdAndUserId(promoCode.getId(), userId);
        if (userUsageCount >= promoCode.getPerUserLimit()) {
            throw new IllegalArgumentException("You have already used this promo code the maximum number of times");
        }

        return promoCode;
    }

    public BigDecimal calculateDiscount(PromoCode promoCode, BigDecimal orderAmount) {
        BigDecimal discount;

        if (promoCode.getDiscountType() == DiscountType.PERCENTAGE) {
            discount = orderAmount.multiply(promoCode.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
            
            // Apply max discount limit if set
            if (promoCode.getMaxDiscountAmount() != null && 
                discount.compareTo(promoCode.getMaxDiscountAmount()) > 0) {
                discount = promoCode.getMaxDiscountAmount();
            }
        } else {
            discount = promoCode.getDiscountValue();
        }

        // Discount cannot exceed order amount
        if (discount.compareTo(orderAmount) > 0) {
            discount = orderAmount;
        }

        return discount;
    }

    public PromoCode applyPromoCode(String code, Long userId) {
        return promoCodeRepository.findByCodeAndIsActiveTrue(code)
                .orElseThrow(() -> new IllegalArgumentException("Invalid promo code"));
    }

    public void recordPromoCodeUsage(PromoCode promoCode, Long userId, Long orderId, BigDecimal discountAmount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        PromoCodeUsage usage = PromoCodeUsage.builder()
                .promoCode(promoCode)
                .user(user)
                .order(null) // Will be set when order is saved
                .discountAmount(discountAmount)
                .build();

        promoCodeUsageRepository.save(usage);
        
        // Increment usage count
        promoCode.incrementUsageCount();
        promoCodeRepository.save(promoCode);

        log.info("Promo code {} applied by user {} for discount of ${}", 
                promoCode.getCode(), userId, discountAmount);
    }

    @Transactional(readOnly = true)
    public PromoCode getPromoCodeByCode(String code) {
        return promoCodeRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Promo code not found"));
    }
}
