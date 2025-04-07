package com.tuhinK.eCommerce.product.controllers;

import com.tuhinK.eCommerce.commons.dtos.ApiResponse;
import com.tuhinK.eCommerce.commons.exceptions.AlreadyExistException;
import com.tuhinK.eCommerce.commons.exceptions.ResourceNotFoundException;
import com.tuhinK.eCommerce.product.dtos.CategoryRequestDto;
import com.tuhinK.eCommerce.product.dtos.CategoryResponseDto;
import com.tuhinK.eCommerce.product.models.Category;
import com.tuhinK.eCommerce.product.services.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RequestMapping("${api_prefix}/categories")
public class CategoryController {

    private final ICategoryService categoryService;

    @Autowired
    public CategoryController(ICategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllCategories() {

        try {
            List<Category> categories = categoryService.getAllCategories();
            return ResponseEntity.ok(new ApiResponse("Categories fetched successfully", categories));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Failed to fetch categories", e.getMessage()));
        }
    }

    @GetMapping("/add")
    public ResponseEntity<ApiResponse> addCategory(@RequestBody CategoryRequestDto categoryRequestDto) {

        try {
            CategoryResponseDto categoryResponse = categoryService.addCategory(categoryRequestDto);
            return ResponseEntity.ok(new ApiResponse("Category added successfully", categoryResponse));
        } catch (AlreadyExistException e) {
            return ResponseEntity.status(CONFLICT).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse> getCategoryById(@PathVariable("categoryId") long categoryId) {

        try {
            Category category = categoryService.getCategoryById(categoryId);
            return ResponseEntity.ok(new ApiResponse("Category fetched successfully", category));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/category/{name}")
    public ResponseEntity<ApiResponse> getCategoryByName(@PathVariable("name") String name) {

        try {
            Category category = categoryService.getCategoryByName(name);
            return ResponseEntity.ok(new ApiResponse("Category fetched successfully", category));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }
    @DeleteMapping("category/{categoryId}/delete")
    public ResponseEntity<ApiResponse> deleteCategory(@PathVariable("categoryId") Long categoryId) {

        try {
            categoryService.deleteCategoryById(categoryId);
            return ResponseEntity.ok(new ApiResponse("Category deleted successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping("category/{categoryId}/update")
    public ResponseEntity<ApiResponse> updateCategory(@PathVariable("categoryId") Long categoryId, @RequestBody CategoryRequestDto categoryRequestDto) {
        try {
            CategoryResponseDto updateCategory = categoryService.updateCategory(categoryRequestDto, categoryId);
            return ResponseEntity.ok(new ApiResponse("Category updated successfully", updateCategory));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

}
