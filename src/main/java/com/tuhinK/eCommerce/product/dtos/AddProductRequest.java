package com.tuhinK.eCommerce.product.dtos;

import com.tuhinK.eCommerce.product.models.Category;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

public class AddProductRequest {
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

    public AddProductRequest setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public AddProductRequest setName(String name) {
        this.name = name;
        return this;
    }

    public String getBrand() {
        return brand;
    }

    public AddProductRequest setBrand(String brand) {
        this.brand = brand;
        return this;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public AddProductRequest setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public int getInventory() {
        return inventory;
    }

    public AddProductRequest setInventory(int inventory) {
        this.inventory = inventory;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public AddProductRequest setDescription(String description) {
        this.description = description;
        return this;
    }

    public Category getCategory() {
        return category;
    }

    public AddProductRequest setCategory(Category category) {
        this.category = category;
        return this;
    }
}
