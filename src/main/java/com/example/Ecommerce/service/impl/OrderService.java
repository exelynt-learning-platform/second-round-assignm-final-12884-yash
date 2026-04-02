package com.example.Ecommerce.service.impl;

import com.example.Ecommerce.entity.*;
import com.example.Ecommerce.exception.custom.BadRequestException;
import com.example.Ecommerce.repository.OrderRepository;
import com.example.Ecommerce.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartService cartService;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public Order placeOrder(Long userId) {

        Cart cart = cartService.getCart(userId);

        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        double total = 0;

        Order order = Order.builder()
                .userId(userId)
                .status("CREATED")
                .createdAt(LocalDateTime.now())
                .build();

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cart.getItems()) {

            Product product = cartItem.getProduct();

            if (product.getStock() < cartItem.getQuantity()) {
                throw new BadRequestException("Insufficient stock for product: " + product.getName());
            }

            product.setStock(product.getStock() - cartItem.getQuantity());

            double itemTotal = product.getPrice() * cartItem.getQuantity();
            total += itemTotal;

            OrderItem orderItem = OrderItem.builder()
                    .productId(product.getId())
                    .quantity(cartItem.getQuantity())
                    .price(product.getPrice())
                    .order(order)
                    .build();

            orderItems.add(orderItem);
        }

        order.setItems(orderItems);
        order.setTotalAmount(total);

        // Save updated stock
        productRepository.saveAll(
                cart.getItems().stream()
                        .map(CartItem::getProduct)
                        .toList()
        );

        Order savedOrder = orderRepository.save(order);

        cartService.clearCart(userId);

        return savedOrder;
    }

    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId);
    }
}