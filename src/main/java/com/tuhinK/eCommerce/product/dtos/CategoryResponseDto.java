package com.tuhinK.eCommerce.product.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

public class CategoryResponseDto {
    private Long id;
    private String name;
    private String description;

    public Long getId() {
        return id;
    }

    public CategoryResponseDto setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public CategoryResponseDto setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public CategoryResponseDto setDescription(String description) {
        this.description = description;
        return this;
    }
}
