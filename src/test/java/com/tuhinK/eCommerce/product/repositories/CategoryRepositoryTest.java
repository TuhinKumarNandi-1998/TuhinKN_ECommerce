package com.tuhinK.eCommerce.product.repositories;

import com.tuhinK.eCommerce.product.models.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();

        Category electronics = new Category();
        electronics.setName("Electronics");
        electronics.setDescription("Electronic items");
        categoryRepository.save(electronics);

        Category clothing = new Category();
        clothing.setName("Clothing");
        clothing.setDescription("Clothing items");
        categoryRepository.save(clothing);
    }

    @Test
    @DisplayName("findByName should return category when found")
    void findByName_found() {
        Category result = categoryRepository.findByName("Electronics");

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Electronics");
        assertThat(result.getDescription()).isEqualTo("Electronic items");
    }

    @Test
    @DisplayName("findByName should return null when not found")
    void findByName_notFound() {
        Category result = categoryRepository.findByName("Furniture");

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("existsByName should return true when category exists")
    void existsByName_true() {
        boolean exists = categoryRepository.existsByName("Electronics");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByName should return false when category does not exist")
    void existsByName_false() {
        boolean exists = categoryRepository.existsByName("Books");

        assertThat(exists).isFalse();
    }
}
