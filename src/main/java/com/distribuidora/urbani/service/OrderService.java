package com.distribuidora.urbani.service;

import com.distribuidora.urbani.dto.OrderItemRequest;
import com.distribuidora.urbani.dto.OrderRequest;
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

    public Order getOrderById(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    public List<Order> getOrdersBySellerAndDate(User seller, LocalDate date) {
        LocalDate targetDate = (date != null) ? date : NOW_ARGENTINA;

        LocalDateTime start = targetDate.atStartOfDay();
        LocalDateTime end = targetDate.atTime(LocalTime.MAX);

        return orderRepository.findBySellerAndCreatedAtBetween(seller, start, end);

    }

    @Transactional
    public Order cancelOrder(UUID id) {
        Order order = getOrderById(id);
        // Solo permitir cancelar si está PENDIENTE
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("No se puede cancelar un pedido que ya fue procesado");
        }
        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }
}
