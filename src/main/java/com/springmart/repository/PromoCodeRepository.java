package com.springmart.repository;

import com.springmart.entity.PromoCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PromoCodeRepository extends JpaRepository<PromoCode, Long> {
    
    Optional<PromoCode> findByCode(String code);
    
    Optional<PromoCode> findByCodeAndIsActiveTrue(String code);
    
    boolean existsByCode(String code);
}
