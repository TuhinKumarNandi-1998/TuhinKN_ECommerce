package com.tuhinK.eCommerce.product.dtos;

import com.tuhinK.eCommerce.product.models.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class CategoryResponseSelf {
    private Category category;
    private String message;

    public Category getCategory() {
        return category;
    }

    public CategoryResponseSelf setCategory(Category category) {
        this.category = category;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public CategoryResponseSelf setMessage(String message) {
        this.message = message;
        return this;
    }
}
