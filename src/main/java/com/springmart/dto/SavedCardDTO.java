package com.springmart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavedCardDTO {
    private Long id;
    private String last4;
    private String brand;
    private String expiryMonth;
    private String expiryYear;
    private String holderName;
    private boolean isDefault;
}
