package com.tuhinK.eCommerce.product.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

public class ProductResponseDto {
    private Long id;
    private String title;
    private BigDecimal price;
    private String description;
    private String category;
    private String image;

    public Long getId() {
        return id;
    }

    public ProductResponseDto setId(Long id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public ProductResponseDto setTitle(String title) {
        this.title = title;
        return this;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public ProductResponseDto setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ProductResponseDto setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getCategory() {
        return category;
    }

    public ProductResponseDto setCategory(String category) {
        this.category = category;
        return this;
    }

    public String getImage() {
        return image;
    }

    public ProductResponseDto setImage(String image) {
        this.image = image;
        return this;
    }
}

