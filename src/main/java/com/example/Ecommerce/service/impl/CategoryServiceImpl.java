package com.example.Ecommerce.service.impl;

import com.example.Ecommerce.dto.request.CategoryRequest;
import com.example.Ecommerce.dto.response.CategoryResponse;
import com.example.Ecommerce.entity.Category;
import com.example.Ecommerce.exception.custom.BadRequestException;
import com.example.Ecommerce.exception.custom.ResourceNotFoundException;
import com.example.Ecommerce.repository.CategoryRepository;
import com.example.Ecommerce.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {

        if (categoryRepository.findByName(request.getName()).isPresent()) {
            throw new BadRequestException("Category already exists");
        }

        Category category = Category.builder()
                .name(request.getName())
                .build();

        Category saved = categoryRepository.save(category);

        return new CategoryResponse(saved.getId(), saved.getName());
    }

    @Override
    public List<CategoryResponse> getALlCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(c -> new CategoryResponse(c.getId(), c.getName()))
                .toList();
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        return new CategoryResponse(category.getId(), category.getName());
    }

    @Override
    public void deleteCategory(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        categoryRepository.delete(category);
    }
}
