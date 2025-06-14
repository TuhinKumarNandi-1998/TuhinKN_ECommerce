package com.tuhinK.eCommerce.product.dtos;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ImageDto {
    private Long id;
    private String fileName;
    private String downloadUrl;
}