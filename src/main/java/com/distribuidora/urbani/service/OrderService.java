package com.distribuidora.urbani.service;

import com.distribuidora.urbani.dto.*;
import com.distribuidora.urbani.entity.*;
import com.distribuidora.urbani.entity.utility.OrderStatus;
import com.distribuidora.urbani.exceptions.ResourceNotFoundException;
import com.distribuidora.urbani.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ClientService clientService;
    private final OrderItemService orderItemService;
    private final ProductService productService;

    final static LocalDateTime NOW_ARGENTINA_TIME = ZonedDateTime
            .now(ZoneId.of("America/Argentina/Buenos_Aires"))
            .toLocalDateTime();

    final static LocalDate NOW_ARGENTINA = ZonedDateTime
            .now(ZoneId.of("America/Argentina/Buenos_Aires"))
            .toLocalDate();


    @Transactional
    public Order createOrder(OrderRequest request, User user) {

        Client client = clientService.getClient(request.clientId());

        Order order = Order.builder()
                .id(UUID.randomUUID())
                .orderNumber(orderRepository.getNextOrderNumber())
                .client(client)
                .seller(user)
                .createdAt(NOW_ARGENTINA_TIME)
                .totalAmount(BigDecimal.ZERO)
                .status(OrderStatus.PENDING)
                .orderItems(new ArrayList<>())
                .build();

        for (OrderItemRequest itemRequest : request.items()) {

            OrderItem orderItem = orderItemService.createOrderItem(itemRequest, order);

            order.getOrderItems().add(orderItem);
            order.setTotalAmount(order.getTotalAmount().add(orderItem.getSubtotal()));

            productService.reduceStock(itemRequest.productId(), itemRequest.quantity());
        }

        return orderRepository.save(order);
    }

    public OrderDetailResponse getOrderById(UUID id) {
        Order o = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        List<OrderItemResponse> orderItemResponsesList = o.getOrderItems().stream()
                .map(orderItem -> new OrderItemResponse(
                        orderItem.getId(),
                        orderItem.getProduct().getName(),
                        orderItem.getQuantity(),
                        orderItem.getUnitPrice(),
                        orderItem.getSubtotal()
                ))
                .toList();

        return new OrderDetailResponse(
                o.getId(),
                o.getOrderNumber(),
                o.getClient().getBusinessName(),
                o.getClient().getId(),
                o.getCreatedAt(),
                o.getTotalAmount(),
                o.getStatus(),
                orderItemResponsesList);
    }

    public List<OrderResponse> getOrdersBySellerAndDate(User seller, LocalDate date) {
        List<Order> orders;

        if (date != null) {
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(LocalTime.MAX);
            orders = orderRepository.findBySellerAndCreatedAtBetween(seller, start, end);
        } else {
            orders = orderRepository.findBySellerOrderByCreatedAtDesc(seller);
        }

        return orders.stream()
                .map(o -> new OrderResponse(
                        o.getId(),
                        o.getOrderNumber(),
                        o.getClient().getBusinessName(),
                        o.getClient().getId(),
                        o.getCreatedAt(),
                        o.getTotalAmount(),
                        o.getStatus(),
                        o.getOrderItems().size()
                ))
                .toList();
    }

    @Transactional
    public Order cancelOrder(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        // Solo permitir cancelar si está PENDIENTE
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("No se puede cancelar un pedido que ya fue procesado");
        }
        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    public List<OrderResponse> getOrdersByClientId(UUID clientId) {
        return orderRepository.findByClientIdOrderByCreatedAtDesc(clientId)
                .stream()
                .map(o -> new OrderResponse(
                        o.getId(),
                        o.getOrderNumber(),
                        o.getClient().getBusinessName(),
                        o.getClient().getId(),
                        o.getCreatedAt(),
                        o.getTotalAmount(),
                        o.getStatus(),
                        o.getOrderItems().size()
                ))
                .toList();
    }
}
