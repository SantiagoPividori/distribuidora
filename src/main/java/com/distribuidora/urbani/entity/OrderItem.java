package com.distribuidora.urbani.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@Entity
@Table(name = "order_items")
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    private UUID id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Order order;
    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    private Product product;
    @Column(nullable = false)
    private int quantity;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;
    @Column(precision = 12, scale = 2)
    private BigDecimal subtotal;
}