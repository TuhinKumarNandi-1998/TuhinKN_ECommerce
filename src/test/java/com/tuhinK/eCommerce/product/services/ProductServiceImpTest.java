package com.tuhinK.eCommerce.product.services;

import com.tuhinK.eCommerce.product.dtos.AddProductRequest;
import com.tuhinK.eCommerce.product.dtos.ImageDto;
import com.tuhinK.eCommerce.product.dtos.ProductDto;
import com.tuhinK.eCommerce.product.dtos.ProductUpdateRequest;
import com.tuhinK.eCommerce.product.exceptions.AlreadyExistsException;
import com.tuhinK.eCommerce.product.exceptions.ProductNotFoundException;
import com.tuhinK.eCommerce.product.models.Category;
import com.tuhinK.eCommerce.product.models.Image;
import com.tuhinK.eCommerce.product.models.Product;
import com.tuhinK.eCommerce.product.repositories.CategoryRepository;
import com.tuhinK.eCommerce.product.repositories.ImageRepository;
import com.tuhinK.eCommerce.product.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImpTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private ProductServiceImp productService;

    private Product product;
    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        category.setDescription("Electronic items");

        product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setBrand("Dell");
        product.setPrice(new BigDecimal("999.99"));
        product.setInventory(10);
        product.setDescription("A powerful laptop");
        product.setCategory(category);
        product.setImages(Collections.emptyList());
    }

    @Nested
    @DisplayName("addProduct")
    class AddProduct {

        @Test
        @DisplayName("should add product successfully when product does not exist and category exists")
        void addProduct_withExistingCategory_success() {
            AddProductRequest request = new AddProductRequest()
                    .setName("Laptop")
                    .setBrand("Dell")
                    .setPrice(new BigDecimal("999.99"))
                    .setInventory(10)
                    .setDescription("A powerful laptop")
                    .setCategory(category);

            when(productRepository.existsByNameAndBrand("Laptop", "Dell")).thenReturn(false);
            when(categoryRepository.findByName("Electronics")).thenReturn(category);
            when(productRepository.save(any(Product.class))).thenReturn(product);

            Product result = productService.addProduct(request);

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Laptop");
            assertThat(result.getBrand()).isEqualTo("Dell");
            verify(productRepository).save(any(Product.class));
        }

        @Test
        @DisplayName("should add product and create new category when category does not exist")
        void addProduct_withNewCategory_createsCategory() {
            Category newCategory = new Category();
            newCategory.setName("Tablets");

            AddProductRequest request = new AddProductRequest()
                    .setName("iPad")
                    .setBrand("Apple")
                    .setPrice(new BigDecimal("799.99"))
                    .setInventory(5)
                    .setDescription("A tablet")
                    .setCategory(newCategory);

            when(productRepository.existsByNameAndBrand("iPad", "Apple")).thenReturn(false);
            when(categoryRepository.findByName("Tablets")).thenReturn(null);
            when(categoryRepository.save(any(Category.class))).thenReturn(newCategory);
            when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

            Product result = productService.addProduct(request);

            assertThat(result).isNotNull();
            verify(categoryRepository).save(any(Category.class));
            verify(productRepository).save(any(Product.class));
        }

        @Test
        @DisplayName("should throw AlreadyExistsException when product with same name and brand exists")
        void addProduct_duplicate_throwsException() {
            AddProductRequest request = new AddProductRequest()
                    .setName("Laptop")
                    .setBrand("Dell")
                    .setCategory(category);

            when(productRepository.existsByNameAndBrand("Laptop", "Dell")).thenReturn(true);

            assertThatThrownBy(() -> productService.addProduct(request))
                    .isInstanceOf(AlreadyExistsException.class)
                    .hasMessageContaining("Dell Laptop already exists");

            verify(productRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("getProductById")
    class GetProductById {

        @Test
        @DisplayName("should return product when found")
        void getProductById_found() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            Product result = productService.getProductById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getName()).isEqualTo("Laptop");
        }

        @Test
        @DisplayName("should throw ProductNotFoundException when not found")
        void getProductById_notFound() {
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.getProductById(99L))
                    .isInstanceOf(ProductNotFoundException.class)
                    .hasMessage("Product not found!");
        }
    }

    @Nested
    @DisplayName("deleteProductById")
    class DeleteProductById {

        @Test
        @DisplayName("should delete product when found")
        void deleteProductById_found() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            productService.deleteProductById(1L);

            verify(productRepository).delete(product);
        }

        @Test
        @DisplayName("should throw ProductNotFoundException when not found")
        void deleteProductById_notFound() {
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.deleteProductById(99L))
                    .isInstanceOf(ProductNotFoundException.class)
                    .hasMessage("Product not found!");

            verify(productRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("updateProduct")
    class UpdateProduct {

        @Test
        @DisplayName("should update product successfully when found")
        void updateProduct_found() {
            ProductUpdateRequest request = new ProductUpdateRequest()
                    .setName("Updated Laptop")
                    .setBrand("Dell")
                    .setPrice(new BigDecimal("1099.99"))
                    .setInventory(15)
                    .setDescription("Updated description")
                    .setCategory(category);

            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(categoryRepository.findByName("Electronics")).thenReturn(category);
            when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

            Product result = productService.updateProduct(request, 1L);

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Updated Laptop");
            assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal("1099.99"));
            assertThat(result.getInventory()).isEqualTo(15);
            verify(productRepository).save(any(Product.class));
        }

        @Test
        @DisplayName("should throw ProductNotFoundException when not found")
        void updateProduct_notFound() {
            ProductUpdateRequest request = new ProductUpdateRequest()
                    .setName("Updated")
                    .setCategory(category);

            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.updateProduct(request, 99L))
                    .isInstanceOf(ProductNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Query methods")
    class QueryMethods {

        @Test
        @DisplayName("getAllProducts should return all products")
        void getAllProducts() {
            when(productRepository.findAll()).thenReturn(List.of(product));

            List<Product> result = productService.getAllProducts();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("Laptop");
        }

        @Test
        @DisplayName("getProductsByCategory should delegate to repository")
        void getProductsByCategory() {
            when(productRepository.findByCategoryName("Electronics")).thenReturn(List.of(product));

            List<Product> result = productService.getProductsByCategory("Electronics");

            assertThat(result).hasSize(1);
            verify(productRepository).findByCategoryName("Electronics");
        }

        @Test
        @DisplayName("getProductsByBrand should delegate to repository")
        void getProductsByBrand() {
            when(productRepository.findByBrand("Dell")).thenReturn(List.of(product));

            List<Product> result = productService.getProductsByBrand("Dell");

            assertThat(result).hasSize(1);
            verify(productRepository).findByBrand("Dell");
        }

        @Test
        @DisplayName("getProductsByCategoryAndBrand should delegate to repository")
        void getProductsByCategoryAndBrand() {
            when(productRepository.findByCategoryNameAndBrand("Electronics", "Dell")).thenReturn(List.of(product));

            List<Product> result = productService.getProductsByCategoryAndBrand("Electronics", "Dell");

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("getProductsByName should delegate to repository")
        void getProductsByName() {
            when(productRepository.findByName("Laptop")).thenReturn(List.of(product));

            List<Product> result = productService.getProductsByName("Laptop");

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("getProductsByBrandAndName should delegate to repository")
        void getProductsByBrandAndName() {
            when(productRepository.findByBrandAndName("Dell", "Laptop")).thenReturn(List.of(product));

            List<Product> result = productService.getProductsByBrandAndName("Dell", "Laptop");

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("countProductsByBrandAndName should return correct count")
        void countProductsByBrandAndName() {
            when(productRepository.countByBrandAndName("Dell", "Laptop")).thenReturn(3L);

            Long count = productService.countProductsByBrandAndName("Dell", "Laptop");

            assertThat(count).isEqualTo(3L);
        }
    }

    @Nested
    @DisplayName("DTO conversion")
    class DtoConversion {

        @Test
        @DisplayName("convertToDto should map product to ProductDto with images")
        void convertToDto_withImages() {
            Image image = new Image();
            image.setId(1L);
            image.setFileName("laptop.jpg");
            image.setDownloadUrl("http://localhost/api/v1/images/1");

            when(imageRepository.findByProductId(1L)).thenReturn(List.of(image));

            ProductDto dto = productService.convertToDto(product);

            assertThat(dto).isNotNull();
            assertThat(dto.getName()).isEqualTo("Laptop");
            assertThat(dto.getBrand()).isEqualTo("Dell");
            assertThat(dto.getImages()).hasSize(1);
            assertThat(dto.getImages().get(0).getFileName()).isEqualTo("laptop.jpg");
        }

        @Test
        @DisplayName("convertToDto should handle product with no images")
        void convertToDto_noImages() {
            when(imageRepository.findByProductId(1L)).thenReturn(Collections.emptyList());

            ProductDto dto = productService.convertToDto(product);

            assertThat(dto).isNotNull();
            assertThat(dto.getImages()).isEmpty();
        }

        @Test
        @DisplayName("getConvertedProducts should convert list of products")
        void getConvertedProducts() {
            when(imageRepository.findByProductId(1L)).thenReturn(Collections.emptyList());

            List<ProductDto> dtos = productService.getConvertedProducts(List.of(product));

            assertThat(dtos).hasSize(1);
            assertThat(dtos.get(0).getName()).isEqualTo("Laptop");
        }
    }
}
