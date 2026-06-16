package com.tuhinK.eCommerce.product.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuhinK.eCommerce.commons.exceptions.ResourceNotFoundException;
import com.tuhinK.eCommerce.product.dtos.AddProductRequest;
import com.tuhinK.eCommerce.product.dtos.ProductDto;
import com.tuhinK.eCommerce.product.dtos.ProductUpdateRequest;
import com.tuhinK.eCommerce.product.exceptions.AlreadyExistsException;
import com.tuhinK.eCommerce.product.exceptions.ProductNotFoundException;
import com.tuhinK.eCommerce.product.models.Category;
import com.tuhinK.eCommerce.product.models.Product;
import com.tuhinK.eCommerce.product.services.ProductService;
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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private Product product;
    private ProductDto productDto;
    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setBrand("Dell");
        product.setPrice(new BigDecimal("999.99"));
        product.setInventory(10);
        product.setDescription("A powerful laptop");
        product.setCategory(category);

        productDto = new ProductDto()
                .setId(1L)
                .setName("Laptop")
                .setBrand("Dell")
                .setPrice(new BigDecimal("999.99"))
                .setInventory(10)
                .setDescription("A powerful laptop")
                .setCategory(category)
                .setImages(Collections.emptyList());
    }

    @Nested
    @DisplayName("GET /api/v1/products/all")
    class GetAllProducts {

        @Test
        @DisplayName("should return all products with 200")
        void getAllProducts_success() throws Exception {
            when(productService.getAllProducts()).thenReturn(List.of(product));
            when(productService.getConvertedProducts(anyList())).thenReturn(List.of(productDto));

            mockMvc.perform(get("/api/v1/products/all"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("success"))
                    .andExpect(jsonPath("$.data[0].name").value("Laptop"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/products/product/{productId}")
    class GetProductById {

        @Test
        @DisplayName("should return product with 200 when found")
        void getProductById_found() throws Exception {
            when(productService.getProductById(1L)).thenReturn(product);
            when(productService.convertToDto(product)).thenReturn(productDto);

            mockMvc.perform(get("/api/v1/products/product/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("success"))
                    .andExpect(jsonPath("$.data.name").value("Laptop"));
        }

        @Test
        @DisplayName("should return 404 when product not found")
        void getProductById_notFound() throws Exception {
            when(productService.getProductById(99L)).thenThrow(new ProductNotFoundException("Product not found!"));

            mockMvc.perform(get("/api/v1/products/product/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Product not found!"))
                    .andExpect(jsonPath("$.data").isEmpty());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/products/add")
    class AddProduct {

        @Test
        @DisplayName("should add product with 200 on success")
        void addProduct_success() throws Exception {
            AddProductRequest request = new AddProductRequest()
                    .setName("Laptop")
                    .setBrand("Dell")
                    .setPrice(new BigDecimal("999.99"))
                    .setInventory(10)
                    .setDescription("A powerful laptop")
                    .setCategory(category);

            when(productService.addProduct(any(AddProductRequest.class))).thenReturn(product);
            when(productService.convertToDto(product)).thenReturn(productDto);

            mockMvc.perform(post("/api/v1/products/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Add product success!"))
                    .andExpect(jsonPath("$.data.name").value("Laptop"));
        }

        @Test
        @DisplayName("should return 409 when product already exists")
        void addProduct_conflict() throws Exception {
            AddProductRequest request = new AddProductRequest()
                    .setName("Laptop")
                    .setBrand("Dell")
                    .setCategory(category);

            when(productService.addProduct(any(AddProductRequest.class)))
                    .thenThrow(new AlreadyExistsException("Dell Laptop already exists"));

            mockMvc.perform(post("/api/v1/products/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("Dell Laptop already exists"));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/products/product/{productId}/update")
    class UpdateProduct {

        @Test
        @DisplayName("should update product with 200 on success")
        void updateProduct_success() throws Exception {
            ProductUpdateRequest request = new ProductUpdateRequest()
                    .setName("Updated Laptop")
                    .setBrand("Dell")
                    .setPrice(new BigDecimal("1099.99"))
                    .setInventory(15)
                    .setDescription("Updated")
                    .setCategory(category);

            when(productService.updateProduct(any(ProductUpdateRequest.class), eq(1L))).thenReturn(product);
            when(productService.convertToDto(product)).thenReturn(productDto);

            mockMvc.perform(put("/api/v1/products/product/1/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Update product success!"));
        }

        @Test
        @DisplayName("should return 404 when product not found")
        void updateProduct_notFound() throws Exception {
            ProductUpdateRequest request = new ProductUpdateRequest()
                    .setName("Updated")
                    .setCategory(category);

            when(productService.updateProduct(any(ProductUpdateRequest.class), eq(99L)))
                    .thenThrow(new ProductNotFoundException("Product not found!"));

            mockMvc.perform(put("/api/v1/products/product/99/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Product not found!"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/products/product/{productId}/delete")
    class DeleteProduct {

        @Test
        @DisplayName("should delete product with 200 on success")
        void deleteProduct_success() throws Exception {
            doNothing().when(productService).deleteProductById(1L);

            mockMvc.perform(delete("/api/v1/products/product/1/delete"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Delete product success!"));
        }

        @Test
        @DisplayName("should return 404 when product not found")
        void deleteProduct_notFound() throws Exception {
            doThrow(new ResourceNotFoundException("Product not found!"))
                    .when(productService).deleteProductById(99L);

            mockMvc.perform(delete("/api/v1/products/product/99/delete"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Product not found!"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/products/products/by/brand-and-name")
    class GetByBrandAndName {

        @Test
        @DisplayName("should return products when found")
        void getByBrandAndName_found() throws Exception {
            when(productService.getProductsByBrandAndName("Dell", "Laptop")).thenReturn(List.of(product));
            when(productService.getConvertedProducts(anyList())).thenReturn(List.of(productDto));

            mockMvc.perform(get("/api/v1/products/products/by/brand-and-name")
                            .param("brandName", "Dell")
                            .param("productName", "Laptop"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("success"));
        }

        @Test
        @DisplayName("should return 404 when no products found")
        void getByBrandAndName_empty() throws Exception {
            when(productService.getProductsByBrandAndName("Unknown", "None")).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/v1/products/products/by/brand-and-name")
                            .param("brandName", "Unknown")
                            .param("productName", "None"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("No products found "));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/products/products/by/category-and-brand")
    class GetByCategoryAndBrand {

        @Test
        @DisplayName("should return products when found")
        void getByCategoryAndBrand_found() throws Exception {
            when(productService.getProductsByCategoryAndBrand("Electronics", "Dell")).thenReturn(List.of(product));
            when(productService.getConvertedProducts(anyList())).thenReturn(List.of(productDto));

            mockMvc.perform(get("/api/v1/products/products/by/category-and-brand")
                            .param("category", "Electronics")
                            .param("brand", "Dell"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("success"));
        }

        @Test
        @DisplayName("should return 404 when no products found")
        void getByCategoryAndBrand_empty() throws Exception {
            when(productService.getProductsByCategoryAndBrand("None", "None")).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/v1/products/products/by/category-and-brand")
                            .param("category", "None")
                            .param("brand", "None"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/products/products/{name}/products")
    class GetByName {

        @Test
        @DisplayName("should return products when found by name")
        void getByName_found() throws Exception {
            when(productService.getProductsByName("Laptop")).thenReturn(List.of(product));
            when(productService.getConvertedProducts(anyList())).thenReturn(List.of(productDto));

            mockMvc.perform(get("/api/v1/products/products/Laptop/products"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("success"));
        }

        @Test
        @DisplayName("should return 404 when no products found by name")
        void getByName_empty() throws Exception {
            when(productService.getProductsByName("NonExistent")).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/v1/products/products/NonExistent/products"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/products/product/by-brand")
    class GetByBrand {

        @Test
        @DisplayName("should return products when found by brand")
        void getByBrand_found() throws Exception {
            when(productService.getProductsByBrand("Dell")).thenReturn(List.of(product));
            when(productService.getConvertedProducts(anyList())).thenReturn(List.of(productDto));

            mockMvc.perform(get("/api/v1/products/product/by-brand")
                            .param("brand", "Dell"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("success"));
        }

        @Test
        @DisplayName("should return 404 when no products found by brand")
        void getByBrand_empty() throws Exception {
            when(productService.getProductsByBrand("Unknown")).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/v1/products/product/by-brand")
                            .param("brand", "Unknown"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/products/product/{category}/all/products")
    class GetByCategory {

        @Test
        @DisplayName("should return products when found by category")
        void getByCategory_found() throws Exception {
            when(productService.getProductsByCategory("Electronics")).thenReturn(List.of(product));
            when(productService.getConvertedProducts(anyList())).thenReturn(List.of(productDto));

            mockMvc.perform(get("/api/v1/products/product/Electronics/all/products"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("success"));
        }

        @Test
        @DisplayName("should return 404 when no products found by category")
        void getByCategory_empty() throws Exception {
            when(productService.getProductsByCategory("Unknown")).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/v1/products/product/Unknown/all/products"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/products/product/count/by-brand/and-name")
    class CountByBrandAndName {

        @Test
        @DisplayName("should return product count")
        void countByBrandAndName_success() throws Exception {
            when(productService.countProductsByBrandAndName("Dell", "Laptop")).thenReturn(5L);

            mockMvc.perform(get("/api/v1/products/product/count/by-brand/and-name")
                            .param("brand", "Dell")
                            .param("name", "Laptop"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Product count!"))
                    .andExpect(jsonPath("$.data").value(5));
        }
    }
}
