package com.tuhinK.eCommerce.product.models;

import com.tuhinK.eCommerce.commons.models.BaseModel;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;


import java.math.BigDecimal;
import java.util.List;

@Data
@Accessors(chain = true)
@Entity
public class Product extends BaseModel {

    private String name;
    private String brand;
    private BigDecimal price;
    private int inventory;
    private String description;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images;

}
