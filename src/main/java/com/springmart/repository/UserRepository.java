package com.springmart.repository;

import com.springmart.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.oauth2Provider = :provider AND u.oauth2Id = :oauth2Id")
    Optional<User> findByOauth2ProviderAndOauth2Id(String provider, String oauth2Id);

    boolean existsByEmail(String email);
}
