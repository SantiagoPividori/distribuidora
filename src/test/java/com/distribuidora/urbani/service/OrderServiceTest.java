package com.distribuidora.urbani.service;

import com.distribuidora.urbani.dto.OrderItemRequest;
import com.distribuidora.urbani.dto.OrderRequest;
import com.distribuidora.urbani.dto.OrderResponse;
import com.distribuidora.urbani.dto.UpdateOrderStatusRequest;
import com.distribuidora.urbani.entity.Client;
import com.distribuidora.urbani.entity.OrderItem;
import com.distribuidora.urbani.entity.User;
import com.distribuidora.urbani.entity.utility.OrderStatus;
import com.distribuidora.urbani.exceptions.InsufficientStockException;
import com.distribuidora.urbani.repository.OrderRepository;
import com.distribuidora.urbani.entity.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ClientService clientService;
    @Mock
    private OrderItemService orderItemService;
    @Mock
    private ProductService productService;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("Tested createOrder and found the happy path, the order was created correctly and returned an Order type.")
    void createOrder_whenCreatedOrderSuccessfully_thenReturnOrder() {

        User userMock = mock(User.class);
        Client clientMock = mock(Client.class);
        Long orderNumberMock = 1L;
        UUID productIdTest1 = UUID.randomUUID();
        UUID productIdTest2 = UUID.randomUUID();
        OrderRequest orderRequestTest = new OrderRequest(UUID.randomUUID(), List.of(
                new OrderItemRequest(productIdTest1, 5),
                new OrderItemRequest(productIdTest2, 10)
        ));
        OrderItem orderItemTest1 = OrderItem.builder()
                .subtotal(BigDecimal.valueOf(1000))
                .build();
        OrderItem orderItemTest2 = OrderItem.builder()
                .subtotal(BigDecimal.valueOf(2500))
                .build();

        when(clientService.getClient(any(UUID.class))).thenReturn(clientMock);
        when(orderRepository.getNextOrderNumber()).thenReturn(orderNumberMock);
        when(orderItemService.createOrderItem(eq(orderRequestTest.items().getFirst()), any(Order.class))).thenReturn(orderItemTest1);
        when(orderItemService.createOrderItem(eq(orderRequestTest.items().getLast()), any(Order.class))).thenReturn(orderItemTest2);
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

        Order orderResult = orderService.createOrder(orderRequestTest, userMock);

        assertNotNull(orderResult.getId());
        assertEquals(orderNumberMock, orderResult.getOrderNumber());
        assertEquals(clientMock, orderResult.getClient());
        assertEquals(userMock, orderResult.getSeller());
        assertTrue(orderResult.getCreatedAt().isBefore(ZonedDateTime
                .now(ZoneId.of("America/Argentina/Buenos_Aires"))
                .toLocalDateTime()));
        assertEquals(BigDecimal.valueOf(3500), orderResult.getTotalAmount());
        assertEquals(OrderStatus.PENDING, orderResult.getStatus());
        assertEquals(List.of(orderItemTest1, orderItemTest2), orderResult.getOrderItems());
        verify(orderItemService, times(1)).createOrderItem(eq(orderRequestTest.items().getFirst()), any(Order.class));
        verify(orderItemService, times(1)).createOrderItem(eq(orderRequestTest.items().getLast()), any(Order.class));
        verify(productService, times(1)).reduceStock(productIdTest1, 5);
        verify(productService, times(1)).reduceStock(productIdTest2, 10);

    }

    @Test
    @DisplayName("Tested createOrder but stock is insufficient for these, needed throw exception")
    void createOrder_whenInsufficientStock_thenThrowsException() {

        User userMock = mock(User.class);
        Client clientMock = mock(Client.class);
        UUID productIdTest1 = UUID.randomUUID();
        OrderRequest orderRequestTest = new OrderRequest(UUID.randomUUID(), List.of(
                new OrderItemRequest(productIdTest1, 5)
        ));

        when(clientService.getClient(any(UUID.class))).thenReturn(clientMock);
        when(orderRepository.getNextOrderNumber()).thenReturn(1L);
        when(orderItemService.createOrderItem(eq(orderRequestTest.items().getFirst()), any(Order.class)))
                .thenThrow(new InsufficientStockException("No hay suficiente stock. Stock disponible: " + 4));


        InsufficientStockException insufficientStockException = assertThrows(InsufficientStockException.class, () -> {
            orderService.createOrder(orderRequestTest, userMock);
        });

        assertEquals(("No hay suficiente stock. Stock disponible: " + 4), insufficientStockException.getMessage());
        verify(orderItemService, times(1)).createOrderItem(eq(orderRequestTest.items().getFirst()), any(Order.class));
        verify(orderRepository, never()).save(any(Order.class));
        verifyNoInteractions(productService);

    }

    @Test
    @DisplayName("Tested updateStatus from pending order to cancel this, the order was cancelled correctly and returned an Order type.")
    void updateStatus_whenPendingOrderIsCancelled_thenReturnOrderCancelled() {

        UUID clientIdTest = UUID.randomUUID();
        UUID orderIdTest = UUID.randomUUID();
        LocalDateTime createdAtTest = LocalDateTime.now();
        UpdateOrderStatusRequest updateOrderStatusRequestTest = new UpdateOrderStatusRequest(OrderStatus.CANCELLED);
        Order orderTest = Order.builder()
                .id(orderIdTest)
                .orderNumber(1L)
                .client(Client.builder()
                        .id(clientIdTest)
                        .businessName("test")
                        .build())
                .createdAt(createdAtTest)
                .totalAmount(BigDecimal.valueOf(3500))
                .status(OrderStatus.PENDING)
                .orderItems(List.of(mock(OrderItem.class)))
                .build();

        when(orderRepository.findById(orderIdTest)).thenReturn(Optional.of(orderTest));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

        OrderResponse orderResponseResult = orderService.updateStatus(orderIdTest, updateOrderStatusRequestTest);

        verify(orderRepository, times(1)).findById(orderIdTest);
        verify(orderRepository, times(1)).save(orderTest);
        assertEquals(orderIdTest, orderResponseResult.id());
        assertEquals(1L,  orderResponseResult.orderNumber());
        assertEquals("test", orderResponseResult.clientBusinessName());
        assertEquals(clientIdTest, orderResponseResult.clientId());
        assertEquals(createdAtTest, orderResponseResult.createdAt());
        assertEquals(BigDecimal.valueOf(3500), orderResponseResult.totalAmount());
        assertEquals(OrderStatus.CANCELLED, orderResponseResult.status());
        assertEquals(1, orderResponseResult.itemCount());
    }



}
