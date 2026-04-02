package com.example.Ecommerce.controller;

import com.example.Ecommerce.dto.PaymentResponse;
import com.example.Ecommerce.service.impl.StripeService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.checkout.Session;
import com.stripe.net.ApiResource;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class StripeController {

    private final StripeService stripeService;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @PostMapping("/create-session/{orderId}")
    public ResponseEntity<PaymentResponse> createCheckoutSession(@PathVariable Long orderId) {
        try {
            PaymentResponse response = stripeService.createCheckoutSession(orderId);
            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }

        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();

        switch (event.getType()) {
            case "checkout.session.completed":
                Session completedSession = (Session) dataObjectDeserializer.getObject().orElse(null);

                if (completedSession == null) {
                    completedSession = ApiResource.GSON.fromJson(dataObjectDeserializer.getRawJson(), Session.class);
                }

                if (completedSession != null) {
                    try {
                        stripeService.fulfillOrder(completedSession.getId());
                    } catch (StripeException e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                }
                break;
            case "checkout.session.async_payment_failed":
            case "checkout.session.expired":
                Session failedSession = (Session) dataObjectDeserializer.getObject().get();
                try {
                    stripeService.cancelOrder(failedSession.getId());
                } catch (StripeException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
                break;
            default:
                break;
        }

        return ResponseEntity.ok("Success");
    }
}
