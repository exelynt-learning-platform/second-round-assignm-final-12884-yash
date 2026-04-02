package com.example.Ecommerce.controller;

import com.example.Ecommerce.entity.Cart;
import com.example.Ecommerce.entity.User;
import com.example.Ecommerce.exception.custom.ResourceNotFoundException;
import com.example.Ecommerce.repository.UserRepository;
import com.example.Ecommerce.service.impl.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    private User getUserFromAuth(Authentication authentication) {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(
            Authentication authentication,
            @RequestParam Long productId,
            @RequestParam int quantity
    ) {

        User user = getUserFromAuth(authentication);

        Cart cart = cartService.addToCart(user.getId(), productId, quantity);

        return ResponseEntity.status(HttpStatus.CREATED).body(cart);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeFromCart(
            Authentication authentication,
            @RequestParam Long productId
    ) {
        User user = getUserFromAuth(authentication);

        cartService.removeFromCart(user.getId(), productId);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateQuantity(
            Authentication authentication,
            @RequestParam Long productId,
            @RequestParam int quantity
    ) {

        User user = getUserFromAuth(authentication);

        Cart cart = cartService.updateQuantity(user.getId(), productId, quantity);

        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart(Authentication authentication) {

        User user = getUserFromAuth(authentication);

        cartService.clearCart(user.getId());

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<?> getCart(Authentication authentication) {

        User user = getUserFromAuth(authentication);

        return ResponseEntity.ok(cartService.getCart(user.getId()));
    }
}