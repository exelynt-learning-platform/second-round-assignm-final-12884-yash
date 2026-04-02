package com.example.Ecommerce.controller;

import com.example.Ecommerce.dto.request.CategoryRequest;
import com.example.Ecommerce.dto.response.ApiResponse;
import com.example.Ecommerce.dto.response.CategoryResponse;
import com.example.Ecommerce.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // ADMIN ONLY
    @PostMapping("/admin")
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryRequest request) {

        CategoryResponse response = categoryService.createCategory(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Category created", response));
    }

    // PUBLIC
    @GetMapping
    public ResponseEntity<?> getAllCategories() {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Categories fetched", categoryService.getALlCategories())
        );
    }

    // PUBLIC
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Category fetched", categoryService.getCategoryById(id))
        );
    }

    // ADMIN ONLY
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {

        categoryService.deleteCategory(id);

        return ResponseEntity.noContent().build();
    }
}
