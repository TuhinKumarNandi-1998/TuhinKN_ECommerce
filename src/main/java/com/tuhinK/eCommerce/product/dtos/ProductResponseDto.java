package com.tuhinK.eCommerce.product.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class ProductResponseDto {
    private Long id;
    private String title;
    private BigDecimal price;
    private String description;
    private String category;
    private String image;
}

