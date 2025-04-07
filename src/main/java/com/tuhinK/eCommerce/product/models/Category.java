package com.tuhinK.eCommerce.product.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tuhinK.eCommerce.commons.models.BaseModel;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@NoArgsConstructor
@Entity
public class Category extends BaseModel {

    private String name;
    private String description;

    @JsonIgnore
    @OneToMany(mappedBy = "category")
    private List<Product> products;

    public String getName() {
        return name;
    }

    public Category setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Category setDescription(String description) {
        this.description = description;
        return this;
    }

    public List<Product> getProducts() {
        return products;
    }

    public Category setProducts(List<Product> products) {
        this.products = products;
        return this;
    }
}
