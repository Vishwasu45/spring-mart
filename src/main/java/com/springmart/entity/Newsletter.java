package com.springmart.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "newsletter_subscriptions")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Newsletter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @CreatedDate
    @Column(name = "subscribed_at", nullable = false, updatable = false)
    private LocalDateTime subscribedAt;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}

