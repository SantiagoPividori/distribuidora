package com.distribuidora.urbani.controller;

import com.distribuidora.urbani.dto.OrderDetailResponse;
import com.distribuidora.urbani.dto.OrderRequest;
import com.distribuidora.urbani.dto.OrderResponse;
import com.distribuidora.urbani.dto.UpdateOrderStatusRequest;
import com.distribuidora.urbani.entity.Order;
import com.distribuidora.urbani.entity.User;
import com.distribuidora.urbani.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController()
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody OrderRequest request, @AuthenticationPrincipal User user) {
        Order savedOrder = orderService.createOrder(request, user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedOrder.getId())
                .toUri();

        return ResponseEntity.created(location).body(savedOrder);

    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderResponse>> getMyOrders(@AuthenticationPrincipal User user, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(orderService.getOrdersBySellerAndDate(user, date));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailResponse> getOrderById(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable UUID id, @RequestBody UpdateOrderStatusRequest updateOrderStatusRequest) {
        return ResponseEntity.ok(orderService.updateStatus(id, updateOrderStatusRequest));
    }

    @GetMapping("/clients/{clientId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByClient(@PathVariable UUID clientId) {
        return ResponseEntity.ok(orderService.getOrdersByClientId(clientId));
    }

}
