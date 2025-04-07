package com.tuhinK.eCommerce.product.services;

import com.tuhinK.eCommerce.commons.exceptions.AlreadyExistException;
import com.tuhinK.eCommerce.commons.exceptions.ResourceNotFoundException;
import com.tuhinK.eCommerce.product.dtos.CategoryRequestDto;
import com.tuhinK.eCommerce.product.dtos.CategoryResponseDto;
import com.tuhinK.eCommerce.product.models.Category;
import com.tuhinK.eCommerce.product.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService implements ICategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    @Override
    public Category getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public CategoryResponseDto addCategory(CategoryRequestDto categoryRequest) {

        if (categoryExist(categoryRequest.getName())) {
            throw new AlreadyExistException(String.format("%s Category already exists", categoryRequest.getName()));
        }

        return createCategoryResponseDto(categoryRepository.save(createCategory(categoryRequest)));
    }

    private CategoryResponseDto createCategoryResponseDto(Category category) {
        return new CategoryResponseDto()
                .setId(category.getId())
                .setName(category.getName())
                .setDescription(category.getDescription());
    }

    private Category createCategory(CategoryRequestDto categoryRequest) {
        return new Category()
                .setName(categoryRequest.getName())
                .setDescription(categoryRequest.getDescription());
    }

    private boolean categoryExist(String categoryName) {
        return categoryRepository.existsByName(categoryName);
    }

    @Override
    public CategoryResponseDto updateCategory(CategoryRequestDto category, Long id) {
        return Optional.ofNullable(getCategoryById(id))
                .map(existingCategory -> {
                    existingCategory.setName(category.getName()).setDescription(category.getDescription());
                    return createCategoryResponseDto(categoryRepository.save(existingCategory));
                })
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    @Override
    public void deleteCategoryById(Long id) {
        categoryRepository.findById(id)
                .ifPresentOrElse(categoryRepository::delete, () -> {
                    throw new ResourceNotFoundException("Category not found");
                });
    }
}
