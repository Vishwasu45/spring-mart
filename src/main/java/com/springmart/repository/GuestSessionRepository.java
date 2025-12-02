package com.springmart.repository;

import com.springmart.entity.GuestSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GuestSessionRepository extends JpaRepository<GuestSession, Long> {
    
    Optional<GuestSession> findBySessionId(String sessionId);
    
    Optional<GuestSession> findByEmail(String email);
    
    List<GuestSession> findByExpiresAtBefore(LocalDateTime dateTime);
}
