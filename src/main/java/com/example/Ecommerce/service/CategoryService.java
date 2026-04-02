package com.example.Ecommerce.service;

import com.example.Ecommerce.dto.request.CategoryRequest;
import com.example.Ecommerce.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest categoryRequest);

    List<CategoryResponse> getALlCategories();

    CategoryResponse getCategoryById(Long id);

    void deleteCategory(Long id);
}
