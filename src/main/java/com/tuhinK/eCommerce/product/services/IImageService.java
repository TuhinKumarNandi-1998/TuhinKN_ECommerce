package com.tuhinK.eCommerce.product.services;

import com.tuhinK.eCommerce.product.dtos.ImageDto;
import com.tuhinK.eCommerce.product.models.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IImageService {
    Image getImageById(Long id);
    void deleteImageById(Long id);
    List<ImageDto> saveImages(Long productId, List<MultipartFile> files);
    void updateImage(MultipartFile file,  Long imageId);
}
