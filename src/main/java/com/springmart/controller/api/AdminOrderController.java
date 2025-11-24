package com.springmart.controller.api;

import com.springmart.dto.OrderDTO;
import com.springmart.enums.OrderStatus;
import com.springmart.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Order Management", description = "Admin APIs for order management")
public class AdminOrderController {

    private final OrderService orderService;

    @PutMapping("/{orderId}/status")
    @Operation(summary = "Update order status manually (Admin only)")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {
        OrderDTO updatedOrder = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(updatedOrder);
    }

    @PostMapping("/{orderId}/cancel")
    @Operation(summary = "Cancel an order (Admin only)")
    public ResponseEntity<OrderDTO> cancelOrder(@PathVariable Long orderId) {
        OrderDTO cancelledOrder = orderService.updateOrderStatus(orderId, OrderStatus.CANCELLED);
        return ResponseEntity.ok(cancelledOrder);
    }
}
