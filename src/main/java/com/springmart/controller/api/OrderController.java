package com.springmart.controller.api;

import com.springmart.dto.CreateOrderRequest;
import com.springmart.dto.OrderDTO;
import com.springmart.entity.OrderStatusHistory;
import com.springmart.enums.OrderStatus;
import com.springmart.security.CustomOAuth2User;
import com.springmart.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order management APIs")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping("/my-orders")
    @Operation(summary = "Get current user's orders")
    public ResponseEntity<Page<OrderDTO>> getMyOrders(
            @AuthenticationPrincipal CustomOAuth2User currentUser,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(orderService.getUserOrders(currentUser.getId(), pageable));
    }

    @GetMapping
    @Operation(summary = "Get all orders (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OrderDTO>> getAllOrders(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(orderService.getAllOrders(pageable));
    }

    @PostMapping
    @Operation(summary = "Create order from cart")
    public ResponseEntity<OrderDTO> createOrder(
            @AuthenticationPrincipal CustomOAuth2User currentUser,
            @Valid @RequestBody CreateOrderRequest request) {
        OrderDTO order = orderService.createOrderFromCart(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update order status (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel order")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/status-history")
    @Operation(summary = "Get order status history")
    public ResponseEntity<List<OrderStatusHistory>> getOrderStatusHistory(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderStatusHistory(id));
    }

    @PutMapping("/{id}/tracking")
    @Operation(summary = "Update order tracking information (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDTO> updateTrackingInfo(
            @PathVariable Long id,
            @RequestParam String trackingNumber,
            @RequestParam String carrier,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime estimatedDelivery) {
        return ResponseEntity.ok(orderService.updateTrackingInfo(id, trackingNumber, carrier, estimatedDelivery));
    }

    @PostMapping("/guest")
    @Operation(summary = "Create guest order")
    public ResponseEntity<OrderDTO> createGuestOrder(
            @CookieValue(value = "guest_session_id") String sessionId,
            @Valid @RequestBody CreateOrderRequest request) {
        OrderDTO order = orderService.createGuestOrder(sessionId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping("/guest/lookup")
    @Operation(summary = "Lookup guest order by order number and email")
    public ResponseEntity<OrderDTO> lookupGuestOrder(
            @RequestParam String orderNumber,
            @RequestParam String email) {
        return ResponseEntity.ok(orderService.getGuestOrderByOrderNumber(orderNumber, email));
    }

    @PostMapping("/{id}/reorder")
    @Operation(summary = "Reorder items from a previous order")
    public ResponseEntity<Void> reorderItems(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomOAuth2User currentUser) {
        orderService.reorderItems(id, currentUser.getId());
        return ResponseEntity.ok().build();
    }
}

