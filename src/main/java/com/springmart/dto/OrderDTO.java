package com.springmart.dto;

import com.springmart.enums.OrderStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {

    private Long id;
    private String orderNumber;

    @NotNull(message = "User ID is required")
    private Long userId;
    private String userName;

    @NotEmpty(message = "Order must contain at least one item")
    private List<OrderItemDTO> items;

    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal shippingCost;
    private BigDecimal totalAmount;

    @NotNull(message = "Order status is required")
    private OrderStatus status;

    private String shippingAddress;
    private String billingAddress;
    private String notes;

    private String trackingNumber;
    private String carrier;
    private LocalDateTime estimatedDeliveryDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
