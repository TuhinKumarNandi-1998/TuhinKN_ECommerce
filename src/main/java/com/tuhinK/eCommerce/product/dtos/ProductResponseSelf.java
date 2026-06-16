package com.tuhinK.eCommerce.product.dtos;

import com.tuhinK.eCommerce.product.models.Product;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ProductResponseSelf {
    private Product product;
    private String message;
}
