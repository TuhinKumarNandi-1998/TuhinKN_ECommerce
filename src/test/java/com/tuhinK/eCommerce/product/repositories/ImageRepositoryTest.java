package com.tuhinK.eCommerce.product.repositories;

import com.tuhinK.eCommerce.product.models.Category;
import com.tuhinK.eCommerce.product.models.Image;
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
class ImageRepositoryTest {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Product savedProduct;

    @BeforeEach
    void setUp() {
        imageRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();

        Category category = new Category();
        category.setName("Electronics");
        category = categoryRepository.save(category);

        Product product = new Product()
                .setName("Laptop")
                .setBrand("Dell")
                .setPrice(new BigDecimal("999.99"))
                .setInventory(10)
                .setDescription("A laptop")
                .setCategory(category);
        savedProduct = productRepository.save(product);

        Image image1 = new Image();
        image1.setFileName("front.jpg");
        image1.setFileType("image/jpeg");
        image1.setDownloadUrl("http://localhost/api/v1/images/1");
        image1.setProduct(savedProduct);
        imageRepository.save(image1);

        Image image2 = new Image();
        image2.setFileName("back.jpg");
        image2.setFileType("image/jpeg");
        image2.setDownloadUrl("http://localhost/api/v1/images/2");
        image2.setProduct(savedProduct);
        imageRepository.save(image2);
    }

    @Test
    @DisplayName("findByProductId should return images for given product")
    void findByProductId_found() {
        List<Image> images = imageRepository.findByProductId(savedProduct.getId());

        assertThat(images).hasSize(2);
        assertThat(images).allMatch(img -> img.getProduct().getId().equals(savedProduct.getId()));
        assertThat(images).extracting(Image::getFileName).containsExactlyInAnyOrder("front.jpg", "back.jpg");
    }

    @Test
    @DisplayName("findByProductId should return empty list for non-existent product")
    void findByProductId_notFound() {
        List<Image> images = imageRepository.findByProductId(999L);

        assertThat(images).isEmpty();
    }
}
