package com.tuhinK.eCommerce.product.repositories;

import com.tuhinK.eCommerce.product.models.Category;
import com.tuhinK.eCommerce.product.models.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category electronics;
    private Category clothing;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();

        electronics = new Category();
        electronics.setName("Electronics");
        electronics.setDescription("Electronic items");
        electronics = categoryRepository.save(electronics);

        clothing = new Category();
        clothing.setName("Clothing");
        clothing.setDescription("Clothing items");
        clothing = categoryRepository.save(clothing);

        Product laptop = new Product()
                .setName("Laptop")
                .setBrand("Dell")
                .setPrice(new BigDecimal("999.99"))
                .setInventory(10)
                .setDescription("A powerful laptop")
                .setCategory(electronics);
        productRepository.save(laptop);

        Product phone = new Product()
                .setName("Phone")
                .setBrand("Samsung")
                .setPrice(new BigDecimal("599.99"))
                .setInventory(20)
                .setDescription("A smartphone")
                .setCategory(electronics);
        productRepository.save(phone);

        Product tshirt = new Product()
                .setName("T-Shirt")
                .setBrand("Nike")
                .setPrice(new BigDecimal("29.99"))
                .setInventory(100)
                .setDescription("A t-shirt")
                .setCategory(clothing);
        productRepository.save(tshirt);

        Product dellMonitor = new Product()
                .setName("Monitor")
                .setBrand("Dell")
                .setPrice(new BigDecimal("299.99"))
                .setInventory(5)
                .setDescription("A Dell monitor")
                .setCategory(electronics);
        productRepository.save(dellMonitor);
    }

    @Test
    @DisplayName("findByCategoryName should return products in given category")
    void findByCategoryName() {
        List<Product> result = productRepository.findByCategoryName("Electronics");

        assertThat(result).hasSize(3);
        assertThat(result).allMatch(p -> p.getCategory().getName().equals("Electronics"));
    }

    @Test
    @DisplayName("findByCategoryName should return empty list for non-existent category")
    void findByCategoryName_notFound() {
        List<Product> result = productRepository.findByCategoryName("Furniture");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByBrand should return products of given brand")
    void findByBrand() {
        List<Product> result = productRepository.findByBrand("Dell");

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(p -> p.getBrand().equals("Dell"));
    }

    @Test
    @DisplayName("findByCategoryNameAndBrand should filter by both")
    void findByCategoryNameAndBrand() {
        List<Product> result = productRepository.findByCategoryNameAndBrand("Electronics", "Dell");

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(p ->
                p.getCategory().getName().equals("Electronics") && p.getBrand().equals("Dell"));
    }

    @Test
    @DisplayName("findByName should perform case-insensitive LIKE search")
    void findByName() {
        List<Product> result = productRepository.findByName("laptop");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Laptop");
    }

    @Test
    @DisplayName("findByName should match partial names")
    void findByName_partial() {
        List<Product> result = productRepository.findByName("top");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Laptop");
    }

    @Test
    @DisplayName("findByBrandAndName should return exact matches")
    void findByBrandAndName() {
        List<Product> result = productRepository.findByBrandAndName("Dell", "Laptop");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Laptop");
        assertThat(result.get(0).getBrand()).isEqualTo("Dell");
    }

    @Test
    @DisplayName("findByBrandAndName should return empty for non-matching combination")
    void findByBrandAndName_noMatch() {
        List<Product> result = productRepository.findByBrandAndName("Dell", "Phone");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("countByBrandAndName should return correct count")
    void countByBrandAndName() {
        Long count = productRepository.countByBrandAndName("Dell", "Laptop");

        assertThat(count).isEqualTo(1L);
    }

    @Test
    @DisplayName("countByBrandAndName should return 0 for no matches")
    void countByBrandAndName_zero() {
        Long count = productRepository.countByBrandAndName("Apple", "Watch");

        assertThat(count).isZero();
    }

    @Test
    @DisplayName("existsByNameAndBrand should return true when product exists")
    void existsByNameAndBrand_true() {
        boolean exists = productRepository.existsByNameAndBrand("Laptop", "Dell");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByNameAndBrand should return false when product does not exist")
    void existsByNameAndBrand_false() {
        boolean exists = productRepository.existsByNameAndBrand("Tablet", "Apple");

        assertThat(exists).isFalse();
    }
}
