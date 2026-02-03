package com.distribuidora.urbani.service;

import com.distribuidora.urbani.dto.OrderItemRequest;
import com.distribuidora.urbani.entity.Order;
import com.distribuidora.urbani.entity.OrderItem;
import com.distribuidora.urbani.entity.Product;
import com.distribuidora.urbani.exceptions.InsufficientStockException;
import com.distribuidora.urbani.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final ProductService productService;

    public OrderItem createOrderItem(OrderItemRequest request, Order order) {

        Product product = productService.getProduct(request.productId());
        if(!productService.stockAvailable(product.getStock(), request.quantity())) {
            throw new InsufficientStockException("No hay suficiente stock. Stock disponible: " + product.getStock());
        }

        return OrderItem.builder()
                .id(UUID.randomUUID())
                .order(order)
                .product(product)
                .quantity(request.quantity())
                .unitPrice(product.getPrice())
                .subtotal(product.getPrice().multiply(BigDecimal.valueOf(request.quantity())))
                .build();
    }

}
