package com.tuhinK.eCommerce.product.services;

import com.tuhinK.eCommerce.commons.exceptions.ResourceNotFoundException;
import com.tuhinK.eCommerce.product.dtos.ImageDto;
import com.tuhinK.eCommerce.product.models.Image;
import com.tuhinK.eCommerce.product.models.Product;
import com.tuhinK.eCommerce.product.repositories.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImageService implements IImageService{

    private final ImageRepository imageRepository;
    private final IProductService productService;

    @Autowired
    public ImageService(ImageRepository imageRepository, IProductService productService) {
        this.imageRepository = imageRepository;
        this.productService = productService;
    }

    @Override
    public Image getImageById(Long id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Image not found %s ", id)));
    }

    @Override
    public void deleteImageById(Long id) {
        imageRepository.findById(id)
                .ifPresentOrElse(imageRepository::delete, () -> {
                    throw new ResourceNotFoundException(String.format("Image not found %s ", id));
                });
    }

    @Override
    public List<ImageDto> saveImages(Long productId, List<MultipartFile> files) {

        Product product = productService.getProductById(productId);
        List<ImageDto> savedImageDto = new ArrayList<>();
        for(MultipartFile file: files){
            try {
                Image image = new Image();
                image.setFileName(file.getOriginalFilename());
                image.setFileType(file.getContentType());
                image.setImage(new SerialBlob(file.getBytes()));
                image.setProduct(product);

                String downloadUri = createDownloadUri(image);
                image.setDownloadUrl(downloadUri);
                Image savedImage = imageRepository.save(image);

                savedImage.setDownloadUrl(createDownloadUri(savedImage));
                imageRepository.save(savedImage);

                ImageDto imageDto = new ImageDto();
                imageDto.setId(savedImage.getId());
                imageDto.setFileName(savedImage.getFileName());
                imageDto.setDownloadUrl(savedImage.getDownloadUrl());
                savedImageDto.add(imageDto);

            } catch (IOException | SQLException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return savedImageDto;
    }

    private String createDownloadUri(Image image){
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/images/")
                .path(image.getId().toString())
                .toUriString();
    }

    @Override
    public void updateImage(MultipartFile file, Long imageId) {

        Image image = getImageById(imageId);
        try {
            image.setFileName(file.getOriginalFilename());
            image.setFileType(file.getContentType());
            image.setImage(new SerialBlob(file.getBytes()));
            imageRepository.save(image);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
