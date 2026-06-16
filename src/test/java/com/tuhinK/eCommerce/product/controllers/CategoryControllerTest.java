package com.tuhinK.eCommerce.product.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuhinK.eCommerce.commons.exceptions.AlreadyExistException;
import com.tuhinK.eCommerce.commons.exceptions.ResourceNotFoundException;
import com.tuhinK.eCommerce.product.dtos.CategoryRequestDto;
import com.tuhinK.eCommerce.product.dtos.CategoryResponseDto;
import com.tuhinK.eCommerce.product.models.Category;
import com.tuhinK.eCommerce.product.services.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    private Category category;
    private CategoryResponseDto categoryResponseDto;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        category.setDescription("Electronic items");

        categoryResponseDto = new CategoryResponseDto()
                .setId(1L)
                .setName("Electronics")
                .setDescription("Electronic items");
    }

    @Nested
    @DisplayName("GET /api/v1/categories/all")
    class GetAllCategories {

        @Test
        @DisplayName("should return all categories with 200")
        void getAllCategories_success() throws Exception {
            when(categoryService.getAllCategories()).thenReturn(List.of(category));

            mockMvc.perform(get("/api/v1/categories/all"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Categories fetched successfully"));
        }

        @Test
        @DisplayName("should return 500 when exception occurs")
        void getAllCategories_error() throws Exception {
            when(categoryService.getAllCategories()).thenThrow(new RuntimeException("DB error"));

            mockMvc.perform(get("/api/v1/categories/all"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value("Failed to fetch categories"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/categories/add")
    class AddCategory {

        @Test
        @DisplayName("should add category with 200 on success")
        void addCategory_success() throws Exception {
            CategoryRequestDto request = new CategoryRequestDto()
                    .setName("Electronics")
                    .setDescription("Electronic items");

            when(categoryService.addCategory(any(CategoryRequestDto.class))).thenReturn(categoryResponseDto);

            mockMvc.perform(get("/api/v1/categories/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Category added successfully"))
                    .andExpect(jsonPath("$.data.name").value("Electronics"));
        }

        @Test
        @DisplayName("should return 409 when category already exists")
        void addCategory_conflict() throws Exception {
            CategoryRequestDto request = new CategoryRequestDto()
                    .setName("Electronics")
                    .setDescription("desc");

            when(categoryService.addCategory(any(CategoryRequestDto.class)))
                    .thenThrow(new AlreadyExistException("Electronics Category already exists"));

            mockMvc.perform(get("/api/v1/categories/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/categories/category/{categoryId}")
    class GetCategoryById {

        @Test
        @DisplayName("should return category with 200 when found")
        void getCategoryById_found() throws Exception {
            when(categoryService.getCategoryById(1L)).thenReturn(category);

            mockMvc.perform(get("/api/v1/categories/category/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Category fetched successfully"));
        }

        @Test
        @DisplayName("should return 404 when category not found")
        void getCategoryById_notFound() throws Exception {
            when(categoryService.getCategoryById(99L)).thenThrow(new ResourceNotFoundException("Category not found"));

            mockMvc.perform(get("/api/v1/categories/category/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Category not found"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/categories/category/name/{name}")
    class GetCategoryByName {

        @Test
        @DisplayName("should return category with 200 when found by name")
        void getCategoryByName_found() throws Exception {
            when(categoryService.getCategoryByName("Electronics")).thenReturn(category);

            mockMvc.perform(get("/api/v1/categories/category/name/Electronics"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Category fetched successfully"))
                    .andExpect(jsonPath("$.data.name").value("Electronics"));
        }

        @Test
        @DisplayName("should return 404 when category not found by name")
        void getCategoryByName_notFound() throws Exception {
            when(categoryService.getCategoryByName("Unknown")).thenThrow(new ResourceNotFoundException("Category not found"));

            mockMvc.perform(get("/api/v1/categories/category/name/Unknown"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Category not found"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/categories/category/{categoryId}/delete")
    class DeleteCategory {

        @Test
        @DisplayName("should delete category with 200 on success")
        void deleteCategory_success() throws Exception {
            doNothing().when(categoryService).deleteCategoryById(1L);

            mockMvc.perform(delete("/api/v1/categories/category/1/delete"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Category deleted successfully"));
        }

        @Test
        @DisplayName("should return 404 when category not found for delete")
        void deleteCategory_notFound() throws Exception {
            doThrow(new ResourceNotFoundException("Category not found"))
                    .when(categoryService).deleteCategoryById(99L);

            mockMvc.perform(delete("/api/v1/categories/category/99/delete"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Category not found"));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/categories/category/{categoryId}/update")
    class UpdateCategory {

        @Test
        @DisplayName("should update category with 200 on success")
        void updateCategory_success() throws Exception {
            CategoryRequestDto request = new CategoryRequestDto()
                    .setName("Updated Electronics")
                    .setDescription("Updated desc");

            CategoryResponseDto updatedResponse = new CategoryResponseDto()
                    .setId(1L)
                    .setName("Updated Electronics")
                    .setDescription("Updated desc");

            when(categoryService.updateCategory(any(CategoryRequestDto.class), eq(1L))).thenReturn(updatedResponse);

            mockMvc.perform(put("/api/v1/categories/category/1/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Category updated successfully"))
                    .andExpect(jsonPath("$.data.name").value("Updated Electronics"));
        }

        @Test
        @DisplayName("should return 404 when category not found for update")
        void updateCategory_notFound() throws Exception {
            CategoryRequestDto request = new CategoryRequestDto()
                    .setName("Updated")
                    .setDescription("desc");

            when(categoryService.updateCategory(any(CategoryRequestDto.class), eq(99L)))
                    .thenThrow(new ResourceNotFoundException("Category not found"));

            mockMvc.perform(put("/api/v1/categories/category/99/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }
    }
}
