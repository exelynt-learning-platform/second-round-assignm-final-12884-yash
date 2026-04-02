package com.example.Ecommerce.controller;

import com.example.Ecommerce.dto.response.ApiResponse;
import com.example.Ecommerce.entity.Order;
import com.example.Ecommerce.service.impl.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/place")
    public ResponseEntity<ApiResponse<Order>> placeOrder(@RequestParam Long userId) {
        Order order = orderService.placeOrder(userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Order placed successfully", order));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Order>>> getOrders(@RequestParam Long userId) {
        List<Order> orders = orderService.getUserOrders(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Orders fetched successfully", orders));
    }
}