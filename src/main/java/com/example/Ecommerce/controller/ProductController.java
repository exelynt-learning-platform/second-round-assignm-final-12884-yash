package com.example.Ecommerce.controller;

import com.example.Ecommerce.dto.request.ProductRequest;
import com.example.Ecommerce.dto.response.ApiResponse;
import com.example.Ecommerce.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // ADMIN
    @PostMapping("/admin")
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Product created",
                        productService.createProduct(request)));
    }

    // ADMIN
    @PutMapping("/admin/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id,
                                           @Valid @RequestBody ProductRequest request) {

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Product updated",
                        productService.updateProduct(id, request))
        );
    }

    // ADMIN
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {

        productService.deleteProduct(id);

        return ResponseEntity.noContent().build();
    }

    // PUBLIC
    @GetMapping
    public ResponseEntity<?> getAllProducts() {

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Products fetched",
                        productService.getAllProducts())
        );
    }

    // PUBLIC
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Product fetched",
                        productService.getProductById(id))
        );
    }
}