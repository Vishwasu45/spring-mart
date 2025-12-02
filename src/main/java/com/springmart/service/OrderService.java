package com.springmart.service;

import com.springmart.dto.CreateOrderRequest;
import com.springmart.dto.OrderDTO;
import com.springmart.dto.OrderItemDTO;
import com.springmart.entity.*;
import com.springmart.enums.OrderStatus;
import com.springmart.exception.InsufficientStockException;
import com.springmart.exception.ResourceNotFoundException;
import com.springmart.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OrderService {

        private final OrderRepository orderRepository;
        private final CartItemRepository cartItemRepository;
        private final ProductRepository productRepository;
        private final UserRepository userRepository;
        private final CartService cartService;
        private final SQSService sqsService;
        private final SNSService snsService;
        private final CloudWatchService cloudWatchService;
        private final OrderStatusHistoryRepository orderStatusHistoryRepository;
        private final PromoCodeService promoCodeService;
        private final GuestCartItemRepository guestCartItemRepository;
        private final PromoCodeRepository promoCodeRepository;

        public OrderDTO getOrderById(Long id) {
                Order order = orderRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
                return convertToDTO(order);
        }

        public Page<OrderDTO> getUserOrders(Long userId, Pageable pageable) {
                return orderRepository.findByUserId(userId, pageable)
                                .map(this::convertToDTO);
        }

        public Page<OrderDTO> getAllOrders(Pageable pageable) {
                return orderRepository.findAll(pageable)
                                .map(this::convertToDTO);
        }

        @Transactional
        public OrderDTO createOrderFromCart(Long userId, CreateOrderRequest request) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

                List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
                if (cartItems.isEmpty()) {
                        throw new IllegalStateException("Cart is empty");
                }

                // Validate stock availability
                for (CartItem cartItem : cartItems) {
                        Product product = cartItem.getProduct();
                        if (product.getStockQuantity() < cartItem.getQuantity()) {
                                throw new InsufficientStockException(
                                                product.getName(),
                                                cartItem.getQuantity(),
                                                product.getStockQuantity());
                        }
                }

                // Calculate totals
                BigDecimal subtotal = cartItems.stream()
                                .map(item -> item.getProduct().getPrice()
                                                .multiply(BigDecimal.valueOf(item.getQuantity())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                // Apply promo code discount if provided
                BigDecimal discountAmount = BigDecimal.ZERO;
                PromoCode promoCode = null;
                if (request.getPromoCode() != null && !request.getPromoCode().isEmpty()) {
                        promoCode = promoCodeService.applyPromoCode(
                                        request.getPromoCode(), userId);
                        discountAmount = promoCodeService.calculateDiscount(promoCode, subtotal);
                }

                BigDecimal subtotalAfterDiscount = subtotal.subtract(discountAmount);
                BigDecimal tax = subtotalAfterDiscount.multiply(BigDecimal.valueOf(0.10)); // 10% tax
                BigDecimal shippingCost = BigDecimal.valueOf(10.00); // Flat shipping
                BigDecimal totalAmount = subtotalAfterDiscount.add(tax).add(shippingCost);

                // Create order
                Order order = Order.builder()
                                .orderNumber(generateOrderNumber())
                                .user(user)
                                .totalAmount(totalAmount)
                                .status(OrderStatus.PENDING)
                                .shippingAddress(request.getShippingAddress())
                                .shippingCity(request.getShippingCity())
                                .shippingState(request.getShippingState())
                                .shippingZip(request.getShippingZip())
                                .shippingCountry(request.getShippingCountry())
                                .promoCode(promoCode)
                                .discountAmount(discountAmount)
                                .build();

                // Create order items and update stock
                List<OrderItem> orderItems = cartItems.stream()
                                .map(cartItem -> {
                                        Product product = cartItem.getProduct();
                                        product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
                                        productRepository.save(product);

                                        return OrderItem.builder()
                                                        .order(order)
                                                        .product(product)
                                                        .quantity(cartItem.getQuantity())
                                                        .price(product.getPrice())
                                                        .build();
                                })
                                .collect(Collectors.toList());

                order.setItems(orderItems);
                Order savedOrder = orderRepository.save(order);

                // Clear cart
                cartService.clearCart(userId);

                log.info("Created order {} for user {}", savedOrder.getOrderNumber(), userId);

                // AWS Integrations
                try {
                        // Send order to SQS for async processing
                        sqsService.sendOrderMessage(savedOrder.getId());

                        // Publish SNS notification
                        snsService.publishOrderPlaced(
                                        savedOrder.getId(),
                                        user.getEmail(),
                                        savedOrder.getTotalAmount().doubleValue());

                        // Record CloudWatch metrics
                        cloudWatchService.recordOrderPlaced(savedOrder.getTotalAmount().doubleValue());
                } catch (Exception e) {
                        log.error("Failed to process AWS integrations for order {}", savedOrder.getId(), e);
                        // Don't fail the order - AWS integrations are supplementary
                }

                return convertToDTO(savedOrder);
        }

        @Transactional
        public OrderDTO updateOrderStatus(Long orderId, OrderStatus newStatus) {
                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

                OrderStatus oldStatus = order.getStatus();
                order.setStatus(newStatus);
                Order updatedOrder = orderRepository.save(order);

                log.info("Updated order {} status to {}", order.getOrderNumber(), newStatus);

                // AWS Integrations - Publish status change notifications
                try {
                        if (newStatus == OrderStatus.SHIPPED) {
                                snsService.publishOrderShipped(
                                                updatedOrder.getId(),
                                                updatedOrder.getUser().getEmail(),
                                                "TRACK-" + updatedOrder.getOrderNumber());
                        } else if (newStatus == OrderStatus.DELIVERED) {
                                snsService.publishOrderDelivered(
                                                updatedOrder.getId(),
                                                updatedOrder.getUser().getEmail());
                                cloudWatchService.recordOrderCompleted();
                        }
                } catch (Exception e) {
                        log.error("Failed to process AWS integrations for order status update", e);
                }

                return convertToDTO(updatedOrder);
        }

        @Transactional
        public void cancelOrder(Long orderId) {
                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

                if (order.getStatus() != OrderStatus.PENDING) {
                        throw new IllegalStateException("Only pending orders can be cancelled");
                }

                // Restore stock
                for (OrderItem item : order.getItems()) {
                        Product product = item.getProduct();
                        product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
                        productRepository.save(product);
                }

                order.setStatus(OrderStatus.CANCELLED);
                orderRepository.save(order);

                log.info("Cancelled order {}", order.getOrderNumber());
        }

        private OrderDTO convertToDTO(Order order) {
                List<OrderItemDTO> items = order.getItems().stream()
                                .map(this::convertOrderItemToDTO)
                                .collect(Collectors.toList());

                // Calculate subtotal from order items
                BigDecimal subtotal = items.stream()
                                .map(OrderItemDTO::getSubtotal)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                // Calculate tax and shipping (same as creation logic)
                BigDecimal tax = subtotal.multiply(BigDecimal.valueOf(0.10));
                BigDecimal shippingCost = BigDecimal.valueOf(10.00);

                return OrderDTO.builder()
                                .id(order.getId())
                                .orderNumber(order.getOrderNumber())
                                .userId(order.getUser().getId())
                                .userName(order.getUser().getName())
                                .items(items)
                                .subtotal(subtotal)
                                .tax(tax)
                                .shippingCost(shippingCost)
                                .totalAmount(order.getTotalAmount())
                                .status(order.getStatus())
                                .shippingAddress(order.getShippingAddress())
                                .billingAddress(null) // Not stored in database
                                .notes(null) // Not stored in database
                                .trackingNumber(order.getTrackingNumber())
                                .carrier(order.getCarrier())
                                .estimatedDeliveryDate(order.getEstimatedDeliveryDate())
                                .createdAt(order.getCreatedAt())
                                .updatedAt(order.getUpdatedAt())
                                .build();
        }

        private OrderItemDTO convertOrderItemToDTO(OrderItem item) {
                String imageUrl = item.getProduct().getImages().stream()
                                .filter(ProductImage::getIsPrimary)
                                .findFirst()
                                .map(ProductImage::getImageUrl)
                                .orElse(null);

                BigDecimal subtotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

                return OrderItemDTO.builder()
                                .id(item.getId())
                                .productId(item.getProduct().getId())
                                .productName(item.getProduct().getName())
                                .productImageUrl(imageUrl)
                                .quantity(item.getQuantity())
                                .price(item.getPrice())
                                .subtotal(subtotal)
                                .build();
        }

        private String generateOrderNumber() {
                return "ORD-" + LocalDateTime.now().getYear() +
                                String.format("%02d", LocalDateTime.now().getMonthValue()) +
                                "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }

        @Transactional
        public OrderDTO updateOrderStatus(Long orderId, OrderStatus newStatus, String notes) {
                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
                
                OrderStatus oldStatus = order.getStatus();
                order.setStatus(newStatus);
                
                // Add to status history
                OrderStatusHistory history = OrderStatusHistory.builder()
                                .order(order)
                                .status(newStatus)
                                .notes(notes != null ? notes : "Status updated from " + oldStatus + " to " + newStatus)
                                .build();
                
                orderStatusHistoryRepository.save(history);
                order = orderRepository.save(order);
                
                log.info("Order {} status updated from {} to {}", orderId, oldStatus, newStatus);
                
                return convertToDTO(order);
        }

        @Transactional
        public OrderDTO updateTrackingInfo(Long orderId, String trackingNumber, String carrier, 
                                          LocalDateTime estimatedDelivery) {
                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
                
                order.setTrackingNumber(trackingNumber);
                order.setCarrier(carrier);
                order.setEstimatedDeliveryDate(estimatedDelivery);
                
                // Add status history entry
                OrderStatusHistory history = OrderStatusHistory.builder()
                                .order(order)
                                .status(order.getStatus())
                                .notes("Tracking information added: " + carrier + " - " + trackingNumber)
                                .build();
                
                orderStatusHistoryRepository.save(history);
                order = orderRepository.save(order);
                
                log.info("Order {} tracking updated: {} - {}", orderId, carrier, trackingNumber);
                
                return convertToDTO(order);
        }

        public List<OrderStatusHistory> getOrderStatusHistory(Long orderId) {
                return orderStatusHistoryRepository.findByOrderIdOrderByCreatedAtAsc(orderId);
        }

        @Transactional
        public OrderDTO createGuestOrder(String guestSessionId, CreateOrderRequest request) {
                // Validate guest session and get cart items
                List<GuestCartItem> cartItems = guestCartItemRepository.findBySessionId(guestSessionId);
                if (cartItems.isEmpty()) {
                        throw new IllegalStateException("Cart is empty");
                }

                // Validate stock availability
                for (GuestCartItem cartItem : cartItems) {
                        Product product = cartItem.getProduct();
                        if (product.getStockQuantity() < cartItem.getQuantity()) {
                                throw new InsufficientStockException(
                                                product.getName(),
                                                cartItem.getQuantity(),
                                                product.getStockQuantity());
                        }
                }

                // Calculate totals
                BigDecimal subtotal = cartItems.stream()
                                .map(item -> item.getProduct().getPrice()
                                                .multiply(BigDecimal.valueOf(item.getQuantity())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                // Apply promo code discount if provided
                BigDecimal discountAmount = BigDecimal.ZERO;
                PromoCode promoCode = null;
                if (request.getPromoCode() != null && !request.getPromoCode().isEmpty()) {
                        // For guest orders, pass null for userId
                        promoCode = promoCodeRepository.findByCodeAndIsActiveTrue(request.getPromoCode())
                                        .orElseThrow(() -> new IllegalArgumentException("Invalid promo code"));
                        
                        if (!promoCode.isValid()) {
                                throw new IllegalArgumentException("Promo code is not valid");
                        }
                        
                        discountAmount = promoCodeService.calculateDiscount(promoCode, subtotal);
                }

                BigDecimal subtotalAfterDiscount = subtotal.subtract(discountAmount);
                BigDecimal tax = subtotalAfterDiscount.multiply(BigDecimal.valueOf(0.10)); // 10% tax
                BigDecimal shippingCost = BigDecimal.valueOf(10.00); // Flat shipping
                BigDecimal totalAmount = subtotalAfterDiscount.add(tax).add(shippingCost);

                // Create order
                Order order = Order.builder()
                                .orderNumber(generateOrderNumber())
                                .guestSessionId(guestSessionId)
                                .guestEmail(request.getGuestEmail())
                                .totalAmount(totalAmount)
                                .status(OrderStatus.PENDING)
                                .shippingAddress(request.getShippingAddress())
                                .shippingCity(request.getShippingCity())
                                .shippingState(request.getShippingState())
                                .shippingZip(request.getShippingZip())
                                .shippingCountry(request.getShippingCountry())
                                .promoCode(promoCode)
                                .discountAmount(discountAmount)
                                .build();

                // Create order items and update stock
                List<OrderItem> orderItems = cartItems.stream()
                                .map(cartItem -> {
                                        Product product = cartItem.getProduct();
                                        product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
                                        productRepository.save(product);

                                        return OrderItem.builder()
                                                        .order(order)
                                                        .product(product)
                                                        .quantity(cartItem.getQuantity())
                                                        .price(product.getPrice())
                                                        .build();
                                })
                                .collect(Collectors.toList());

                order.setItems(orderItems);
                Order savedOrder = orderRepository.save(order);

                // Clear guest cart
                guestCartItemRepository.deleteBySessionId(guestSessionId);

                log.info("Created guest order {} for session {}", savedOrder.getOrderNumber(), guestSessionId);

                // AWS Integrations
                try {
                        // Send order to SQS for async processing
                        sqsService.sendOrderMessage(savedOrder.getId());

                        // Publish SNS notification
                        snsService.publishOrderPlaced(
                                        savedOrder.getId(),
                                        savedOrder.getOrderNumber(),
                                        savedOrder.getTotalAmount().doubleValue());

                        // Log CloudWatch metric
                        cloudWatchService.recordOrderPlaced(savedOrder.getTotalAmount().doubleValue());
                } catch (Exception e) {
                        log.error("Failed to process AWS integrations for order {}", savedOrder.getId(), e);
                        // Continue even if AWS integrations fail
                }

                return convertToDTO(savedOrder);
        }

        public OrderDTO getGuestOrderByOrderNumber(String orderNumber, String guestEmail) {
                Order order = orderRepository.findByOrderNumberAndGuestEmail(orderNumber, guestEmail)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Order", "orderNumber", orderNumber));
                return convertToDTO(order);
        }

        @Transactional
        public void reorderItems(Long orderId, Long userId) {
                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
                
                // Verify order belongs to user
                if (order.getUser() == null || !order.getUser().getId().equals(userId)) {
                        throw new IllegalArgumentException("Order does not belong to user");
                }
                
                int addedItems = 0;
                int outOfStockItems = 0;
                
                for (OrderItem orderItem : order.getItems()) {
                        Product product = orderItem.getProduct();
                        
                        // Check if product is still available and in stock
                        if (!product.getIsActive() || product.getStockQuantity() < 1) {
                                outOfStockItems++;
                                continue;
                        }
                        
                        // Try to add to cart
                        try {
                                cartService.addToCart(userId, product.getId(), orderItem.getQuantity());
                                addedItems++;
                        } catch (Exception e) {
                                log.warn("Failed to add product {} to cart during reorder: {}", 
                                        product.getId(), e.getMessage());
                                outOfStockItems++;
                        }
                }
                
                log.info("Reordered {} items from order {} for user {}. {} items were out of stock.",
                        addedItems, orderId, userId, outOfStockItems);
                
                if (addedItems == 0) {
                        throw new IllegalStateException("None of the items from this order are currently available");
                }
        }
}
