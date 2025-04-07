package com.tuhinK.eCommerce.product.dtos;

import com.tuhinK.eCommerce.product.models.Category;
import lombok.Data;

import java.math.BigDecimal;

public class ProductUpdateRequest {
    private Long id;
    private String name;
    private String brand;
    private BigDecimal price;
    private int inventory;
    private String description;
    private Category category;

    public Long getId() {
        return id;
    }

    public ProductUpdateRequest setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public ProductUpdateRequest setName(String name) {
        this.name = name;
        return this;
    }

    public String getBrand() {
        return brand;
    }

    public ProductUpdateRequest setBrand(String brand) {
        this.brand = brand;
        return this;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public ProductUpdateRequest setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public int getInventory() {
        return inventory;
    }

    public ProductUpdateRequest setInventory(int inventory) {
        this.inventory = inventory;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ProductUpdateRequest setDescription(String description) {
        this.description = description;
        return this;
    }

    public Category getCategory() {
        return category;
    }

    public ProductUpdateRequest setCategory(Category category) {
        this.category = category;
        return this;
    }
}