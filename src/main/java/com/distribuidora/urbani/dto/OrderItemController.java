package com.distribuidora.urbani.dto;

import com.distribuidora.urbani.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;



}
