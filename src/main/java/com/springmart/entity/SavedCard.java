package com.springmart.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "saved_cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavedCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "card_number_last4", nullable = false, length = 4)
    private String last4;

    @Column(nullable = false)
    private String brand; // Visa, Mastercard, etc.

    @Column(name = "expiry_month", nullable = false)
    private String expiryMonth;

    @Column(name = "expiry_year", nullable = false)
    private String expiryYear;

    @Column(name = "holder_name", nullable = false)
    private String holderName;

    @Column(name = "is_default")
    @Builder.Default
    private Boolean isDefault = false;
}
