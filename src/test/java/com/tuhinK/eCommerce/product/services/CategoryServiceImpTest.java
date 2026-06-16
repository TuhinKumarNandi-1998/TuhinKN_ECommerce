package com.tuhinK.eCommerce.product.services;

import com.tuhinK.eCommerce.commons.exceptions.AlreadyExistException;
import com.tuhinK.eCommerce.commons.exceptions.ResourceNotFoundException;
import com.tuhinK.eCommerce.product.dtos.CategoryRequestDto;
import com.tuhinK.eCommerce.product.dtos.CategoryResponseDto;
import com.tuhinK.eCommerce.product.models.Category;
import com.tuhinK.eCommerce.product.repositories.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImpTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImp categoryService;

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        category.setDescription("Electronic items");
    }

    @Nested
    @DisplayName("getCategoryById")
    class GetCategoryById {

        @Test
        @DisplayName("should return category when found")
        void getCategoryById_found() {
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

            Category result = categoryService.getCategoryById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getName()).isEqualTo("Electronics");
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when not found")
        void getCategoryById_notFound() {
            when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> categoryService.getCategoryById(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Category not found");
        }
    }

    @Nested
    @DisplayName("getCategoryByName")
    class GetCategoryByName {

        @Test
        @DisplayName("should return category when found by name")
        void getCategoryByName_found() {
            when(categoryRepository.findByName("Electronics")).thenReturn(category);

            Category result = categoryService.getCategoryByName("Electronics");

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Electronics");
        }

        @Test
        @DisplayName("should return null when category name not found")
        void getCategoryByName_notFound() {
            when(categoryRepository.findByName("NonExistent")).thenReturn(null);

            Category result = categoryService.getCategoryByName("NonExistent");

            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("getAllCategories")
    class GetAllCategories {

        @Test
        @DisplayName("should return all categories")
        void getAllCategories_returnsList() {
            Category category2 = new Category();
            category2.setId(2L);
            category2.setName("Clothing");

            when(categoryRepository.findAll()).thenReturn(List.of(category, category2));

            List<Category> result = categoryService.getAllCategories();

            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("should return empty list when no categories exist")
        void getAllCategories_empty() {
            when(categoryRepository.findAll()).thenReturn(List.of());

            List<Category> result = categoryService.getAllCategories();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("addCategory")
    class AddCategory {

        @Test
        @DisplayName("should add category successfully when it does not exist")
        void addCategory_success() {
            CategoryRequestDto request = new CategoryRequestDto()
                    .setName("Books")
                    .setDescription("Book items");

            Category savedCategory = new Category();
            savedCategory.setId(2L);
            savedCategory.setName("Books");
            savedCategory.setDescription("Book items");

            when(categoryRepository.existsByName("Books")).thenReturn(false);
            when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

            CategoryResponseDto result = categoryService.addCategory(request);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(2L);
            assertThat(result.getName()).isEqualTo("Books");
            assertThat(result.getDescription()).isEqualTo("Book items");
            verify(categoryRepository).save(any(Category.class));
        }

        @Test
        @DisplayName("should throw AlreadyExistException when category name already exists")
        void addCategory_duplicate() {
            CategoryRequestDto request = new CategoryRequestDto()
                    .setName("Electronics")
                    .setDescription("Electronic items");

            when(categoryRepository.existsByName("Electronics")).thenReturn(true);

            assertThatThrownBy(() -> categoryService.addCategory(request))
                    .isInstanceOf(AlreadyExistException.class);

            verify(categoryRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("updateCategory")
    class UpdateCategory {

        @Test
        @DisplayName("should update category successfully when found")
        void updateCategory_success() {
            CategoryRequestDto request = new CategoryRequestDto()
                    .setName("Updated Electronics")
                    .setDescription("Updated description");

            Category updatedCategory = new Category();
            updatedCategory.setId(1L);
            updatedCategory.setName("Updated Electronics");
            updatedCategory.setDescription("Updated description");

            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);

            CategoryResponseDto result = categoryService.updateCategory(request, 1L);

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Updated Electronics");
            assertThat(result.getDescription()).isEqualTo("Updated description");
            verify(categoryRepository).save(any(Category.class));
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when category not found for update")
        void updateCategory_notFound() {
            CategoryRequestDto request = new CategoryRequestDto()
                    .setName("Updated")
                    .setDescription("Desc");

            when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> categoryService.updateCategory(request, 99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("deleteCategoryById")
    class DeleteCategoryById {

        @Test
        @DisplayName("should delete category when found")
        void deleteCategoryById_found() {
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

            categoryService.deleteCategoryById(1L);

            verify(categoryRepository).delete(category);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when not found")
        void deleteCategoryById_notFound() {
            when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> categoryService.deleteCategoryById(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Category not found");

            verify(categoryRepository, never()).delete(any());
        }
    }
}
