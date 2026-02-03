package com.distribuidora.urbani.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product {

    @Id
    private UUID id;
    @Column(name = "name", unique = true)
    private String name;
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    @Column(name = "price", nullable = false,  scale = 2, precision = 19)
    private BigDecimal price;
    @Column(name = "stock", nullable = false)
    private int stock;

}
