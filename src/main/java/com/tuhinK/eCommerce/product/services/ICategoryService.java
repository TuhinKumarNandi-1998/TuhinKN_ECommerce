package com.tuhinK.eCommerce.product.services;

import com.tuhinK.eCommerce.product.dtos.CategoryRequestDto;
import com.tuhinK.eCommerce.product.dtos.CategoryResponseDto;
import com.tuhinK.eCommerce.product.models.Category;

import java.util.List;

public interface ICategoryService {
    Category getCategoryById(Long id);
    Category getCategoryByName(String name);
    List<Category> getAllCategories();
    CategoryResponseDto addCategory(CategoryRequestDto categoryRequest);
    CategoryResponseDto updateCategory(CategoryRequestDto categoryRequest, Long id);
    void deleteCategoryById(Long id);
}
