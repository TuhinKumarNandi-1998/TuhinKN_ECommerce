package com.tuhinK.eCommerce.product.dtos;

import com.tuhinK.eCommerce.product.models.Category;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Data
@Accessors(chain = true)
public class ProductDto {
    private Long id;
    private String name;
    private String brand;
    private BigDecimal price;
    private int inventory;
    private String description;
    private Category category;
    private List<ImageDto> images;

    // public Long getId() {
    //     return id;
    // }

    // public ProductDto setId(Long id) {
    //     this.id = id;
    //     return this;
    // }

    // public String getName() {
    //     return name;
    // }

    // public ProductDto setName(String name) {
    //     this.name = name;
    //     return this;
    // }

    // public String getBrand() {
    //     return brand;
    // }

    // public ProductDto setBrand(String brand) {
    //     this.brand = brand;
    //     return this;
    // }

    // public BigDecimal getPrice() {
    //     return price;
    // }

    // public ProductDto setPrice(BigDecimal price) {
    //     this.price = price;
    //     return this;
    // }

    // public int getInventory() {
    //     return inventory;
    // }

    // public ProductDto setInventory(int inventory) {
    //     this.inventory = inventory;
    //     return this;
    // }

    // public String getDescription() {
    //     return description;
    // }

    // public ProductDto setDescription(String description) {
    //     this.description = description;
    //     return this;
    // }

    // public Category getCategory() {
    //     return category;
    // }

    // public ProductDto setCategory(Category category) {
    //     this.category = category;
    //     return this;
    // }

    // public List<ImageDto> getImages() {
    //     return images;
    // }

    // public ProductDto setImages(List<ImageDto> images) {
    //     this.images = images;
    //     return this;
    // }
}