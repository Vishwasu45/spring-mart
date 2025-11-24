package com.springmart.service;

import com.springmart.entity.Order;
import com.springmart.enums.OrderStatus;
import com.springmart.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service to handle automatic order status transitions based on time.
 * Simulates the order lifecycle from PENDING to DELIVERED.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderStatusScheduler {

    private final OrderRepository orderRepository;

    /**
     * Runs every 30 seconds to check and update order statuses.
     * Order lifecycle:
     * - PENDING -> CONFIRMED (after 1 minute)
     * - CONFIRMED -> PROCESSING (after 2 minutes)
     * - PROCESSING -> SHIPPED (after 5 minutes)
     * - SHIPPED -> DELIVERED (after 10 minutes)
     */
    @Scheduled(fixedRate = 30000) // Run every 30 seconds
    @Transactional
    public void updateOrderStatuses() {
        LocalDateTime now = LocalDateTime.now();

        // PENDING -> CONFIRMED (after 1 minute)
        List<Order> pendingOrders = orderRepository.findByStatus(OrderStatus.PENDING);
        for (Order order : pendingOrders) {
            if (order.getCreatedAt().plusMinutes(1).isBefore(now)) {
                order.setStatus(OrderStatus.CONFIRMED);
                orderRepository.save(order);
                log.info("Order {} transitioned from PENDING to CONFIRMED", order.getOrderNumber());
            }
        }

        // CONFIRMED -> PROCESSING (after 2 minutes from creation)
        List<Order> confirmedOrders = orderRepository.findByStatus(OrderStatus.CONFIRMED);
        for (Order order : confirmedOrders) {
            if (order.getCreatedAt().plusMinutes(2).isBefore(now)) {
                order.setStatus(OrderStatus.PROCESSING);
                orderRepository.save(order);
                log.info("Order {} transitioned from CONFIRMED to PROCESSING", order.getOrderNumber());
            }
        }

        // PROCESSING -> SHIPPED (after 5 minutes from creation)
        List<Order> processingOrders = orderRepository.findByStatus(OrderStatus.PROCESSING);
        for (Order order : processingOrders) {
            if (order.getCreatedAt().plusMinutes(5).isBefore(now)) {
                order.setStatus(OrderStatus.SHIPPED);
                orderRepository.save(order);
                log.info("Order {} transitioned from PROCESSING to SHIPPED", order.getOrderNumber());
            }
        }

        // SHIPPED -> DELIVERED (after 10 minutes from creation)
        List<Order> shippedOrders = orderRepository.findByStatus(OrderStatus.SHIPPED);
        for (Order order : shippedOrders) {
            if (order.getCreatedAt().plusMinutes(10).isBefore(now)) {
                order.setStatus(OrderStatus.DELIVERED);
                orderRepository.save(order);
                log.info("Order {} transitioned from SHIPPED to DELIVERED", order.getOrderNumber());
            }
        }
    }
}
