package com.tuhinK.eCommerce.product.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tuhinK.eCommerce.commons.models.BaseModel;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@Entity
public class Category extends BaseModel {

    private String name;
    private String description;

    @JsonIgnore
    @OneToMany(mappedBy = "category")
    private List<Product> products;
}
