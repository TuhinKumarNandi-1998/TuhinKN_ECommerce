package com.tuhinK.eCommerce.product.models;

import com.tuhinK.eCommerce.commons.models.BaseModel;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Blob;

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

    public String getFileName() {
        return fileName;
    }

    public Image setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public String getFileType() {
        return fileType;
    }

    public Image setFileType(String fileType) {
        this.fileType = fileType;
        return this;
    }

    public Blob getImage() {
        return image;
    }

    public Image setImage(Blob image) {
        this.image = image;
        return this;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public Image setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
        return this;
    }

    public Product getProduct() {
        return product;
    }

    public Image setProduct(Product product) {
        this.product = product;
        return this;
    }
}
