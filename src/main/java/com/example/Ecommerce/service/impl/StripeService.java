package com.example.Ecommerce.service.impl;

import com.example.Ecommerce.dto.PaymentResponse;
import com.example.Ecommerce.entity.Order;
import com.example.Ecommerce.repository.OrderRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StripeService {

    private final OrderRepository orderRepository;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    public PaymentResponse createCheckoutSession(long orderId) throws StripeException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        // Use a success/cancel URL that the user can copy into their browser from Postman
        // In a real app, these would point to your frontend
        String successUrl = "https://example.com/success?session_id={CHECKOUT_SESSION_ID}";
        String cancelUrl = "https://example.com/cancel";

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmount((long) (order.getTotalAmount() * 100)) // Stripe expects amount in cents
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Order #" + order.getId())
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .putMetadata("orderId", String.valueOf(orderId))
                .build();

        Session session = Session.create(params);

        return PaymentResponse.builder()
                .paymentUrl(session.getUrl())
                .sessionId(session.getId())
                .build();
    }

    public void fulfillOrder(String sessionId) throws StripeException {
        Session session = Session.retrieve(sessionId);
        String orderIdStr = session.getMetadata().get("orderId");

        if (orderIdStr != null) {
            long orderId = Long.parseLong(orderIdStr);
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found for fulfillment: " + orderId));
            
            order.setStatus("PAID");
            orderRepository.save(order);
        }
    }

    public void cancelOrder(String sessionId) throws StripeException {
        Session session = Session.retrieve(sessionId);
        String orderIdStr = session.getMetadata().get("orderId");

        if (orderIdStr != null) {
            long orderId = Long.parseLong(orderIdStr);
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found for cancellation: " + orderId));
            
            order.setStatus("CANCELLED");
            orderRepository.save(order);
        }
    }
}
