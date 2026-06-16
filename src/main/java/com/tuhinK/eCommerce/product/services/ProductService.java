package com.tuhinK.eCommerce.product.services;

import com.tuhinK.eCommerce.product.dtos.AddProductRequest;
import com.tuhinK.eCommerce.product.dtos.ProductDto;
import com.tuhinK.eCommerce.product.dtos.ProductUpdateRequest;
import com.tuhinK.eCommerce.product.exceptions.ProductNotFoundException;
import com.tuhinK.eCommerce.product.models.Product;

import java.util.List;

public interface ProductService {

    Product addProduct(AddProductRequest product);

    Product getProductById(Long id) throws ProductNotFoundException;

    void deleteProductById(Long id) throws ProductNotFoundException;

    Product updateProduct(ProductUpdateRequest product, Long productId) throws ProductNotFoundException;

    List<Product> getAllProducts();

    List<Product> getProductsByCategory(String category);

    List<Product> getProductsByBrand(String brand)  throws ProductNotFoundException;

    List<Product> getProductsByCategoryAndBrand(String category, String brand) throws ProductNotFoundException;

    List<Product> getProductsByName(String name) throws ProductNotFoundException;

    List<Product> getProductsByBrandAndName(String category, String name) throws ProductNotFoundException;

    Long countProductsByBrandAndName(String brand, String name);

    List<ProductDto> getConvertedProducts(List<Product> products);

    ProductDto convertToDto(Product product);
}

