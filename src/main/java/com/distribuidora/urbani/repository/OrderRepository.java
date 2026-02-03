package com.distribuidora.urbani.repository;

import com.distribuidora.urbani.entity.Order;
import com.distribuidora.urbani.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    @Query("SELECT COALESCE(MAX(o.orderNumber), 0) + 1 FROM Order o")
    Long getNextOrderNumber();

    List<Order> findBySellerAndCreatedAtBetween(User seller, LocalDateTime start, LocalDateTime end);

}
