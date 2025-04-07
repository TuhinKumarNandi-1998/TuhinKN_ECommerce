package com.tuhinK.eCommerce.product.dtos;

import lombok.Data;

public class ImageDto {
    private Long id;
    private String fileName;
    private String downloadUrl;

    public Long getId() {
        return id;
    }

    public ImageDto setId(Long id) {
        this.id = id;
        return this;
    }

    public String getFileName() {
        return fileName;
    }

    public ImageDto setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public ImageDto setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
        return this;
    }
}