package com.springmart.repository;

import com.springmart.entity.GuestCartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuestCartItemRepository extends JpaRepository<GuestCartItem, Long> {
    
    List<GuestCartItem> findBySessionId(String sessionId);
    
    Optional<GuestCartItem> findBySessionIdAndProductId(String sessionId, Long productId);
    
    void deleteBySessionId(String sessionId);
    
    long countBySessionId(String sessionId);
}
