package com.tuhinK.eCommerce.product.dtos;

import com.tuhinK.eCommerce.product.models.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class ProductResponseSelf {
    private Product product;
    private String message;

    public Product getProduct() {
        return product;
    }

    public ProductResponseSelf setProduct(Product product) {
        this.product = product;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ProductResponseSelf setMessage(String message) {
        this.message = message;
        return this;
    }
}
