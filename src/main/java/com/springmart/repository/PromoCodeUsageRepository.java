package com.springmart.repository;

import com.springmart.entity.PromoCodeUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromoCodeUsageRepository extends JpaRepository<PromoCodeUsage, Long> {
    
    long countByPromoCodeIdAndUserId(Long promoCodeId, Long userId);
    
    boolean existsByPromoCodeIdAndUserId(Long promoCodeId, Long userId);
}
