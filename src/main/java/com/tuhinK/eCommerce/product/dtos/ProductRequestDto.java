package com.tuhinK.eCommerce.product.dtos;

import com.tuhinK.eCommerce.product.models.Category;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

public class ProductRequestDto {
    private String name;
    private String brand;
    private BigDecimal price;
    private int inventory;
    private String description;
    private Category category;
    private List<ImageDto> images;

    public String getName() {
        return name;
    }

    public ProductRequestDto setName(String name) {
        this.name = name;
        return this;
    }

    public String getBrand() {
        return brand;
    }

    public ProductRequestDto setBrand(String brand) {
        this.brand = brand;
        return this;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public ProductRequestDto setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public int getInventory() {
        return inventory;
    }

    public ProductRequestDto setInventory(int inventory) {
        this.inventory = inventory;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ProductRequestDto setDescription(String description) {
        this.description = description;
        return this;
    }

    public Category getCategory() {
        return category;
    }

    public ProductRequestDto setCategory(Category category) {
        this.category = category;
        return this;
    }

    public List<ImageDto> getImages() {
        return images;
    }

    public ProductRequestDto setImages(List<ImageDto> images) {
        this.images = images;
        return this;
    }
}

