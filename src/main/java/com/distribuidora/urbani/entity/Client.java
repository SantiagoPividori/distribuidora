package com.distribuidora.urbani.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "clients")
public class Client {

    @Id
    private UUID id;
    @Column(name = "business_name", unique = true, nullable = false)
    private String businessName;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "address", nullable = false)
    private String address;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "tax_id")
    private String taxId;
    @Column(name = "active")
    private boolean active;

}
