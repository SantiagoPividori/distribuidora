package com.distribuidora.urbani.entity;

import com.distribuidora.urbani.entity.utility.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    private UUID id;
    @Column(name = "order_number", unique = true, nullable = false)
    private Long orderNumber;
    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id")
    private Client client;
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User seller;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(precision = 12, scale = 2)
    private BigDecimal totalAmount;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

}
