package com.tuhinK.eCommerce.product.models;

import com.tuhinK.eCommerce.commons.models.BaseModel;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.sql.Blob;

@Data
@Accessors(chain = true)
@Entity
public class Image extends BaseModel {

    private String fileName;
    private String fileType;

    @Lob
    private Blob image;
    private String downloadUrl;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
