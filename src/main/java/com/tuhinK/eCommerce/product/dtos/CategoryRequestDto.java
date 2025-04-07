package com.tuhinK.eCommerce.product.dtos;

import lombok.Data;

public class CategoryRequestDto {
    private String name;
    private String description;

    public String getName() {
        return name;
    }

    public CategoryRequestDto setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public CategoryRequestDto setDescription(String description) {
        this.description = description;
        return this;
    }
}
