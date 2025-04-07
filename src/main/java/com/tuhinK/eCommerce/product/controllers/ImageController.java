package com.tuhinK.eCommerce.product.controllers;

import com.tuhinK.eCommerce.commons.dtos.ApiResponse;
import com.tuhinK.eCommerce.commons.exceptions.ResourceNotFoundException;
import com.tuhinK.eCommerce.product.dtos.ImageDto;
import com.tuhinK.eCommerce.product.models.Image;
import com.tuhinK.eCommerce.product.services.IImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("${api_prefix}/images")
public class ImageController {

    private final IImageService imageService;

    @Autowired
    public ImageController(IImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse> saveImages(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("productId") long productId) {

        try {
            List<MultipartFile> filesList = Arrays.asList(files);
            List<ImageDto> imageDtos = imageService.saveImages(productId, filesList);
            return ResponseEntity.ok(new ApiResponse("Upload success", imageDtos));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Upload failed", e.getMessage()));
        }
    }

    @GetMapping("/download/{imageId}")
    public ResponseEntity<Resource> downloadImage(@PathVariable("imageId") long imageId) {
        try {
            Image image = imageService.getImageById(imageId);
            if (image == null || image.getImage() == null) {
                return ResponseEntity.notFound().build();
            }

            // Safely extract bytes from Blob
            byte[] data;
            try (java.io.InputStream is = image.getImage().getBinaryStream()) {
                data = is.readAllBytes();
            }

            ByteArrayResource resource = new ByteArrayResource(data);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(image.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getFileName() + "\"")
                    .body(resource);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (SQLException | IOException e) {
            // Log the exception for debugging
            // logger.error("Error downloading image", e);
            throw new RuntimeException("Error downloading image: " + e.getMessage(), e);
        }
    }

    @PutMapping("/{imageId}")
    public ResponseEntity<ApiResponse> updateImage(
            @PathVariable("imageId") long imageId,
            @RequestParam("file") MultipartFile file) {

        try {
            // First check if image exists
            imageService.getImageById(imageId);

            // If we get here, the image exists, so update it
            imageService.updateImage(file, imageId);
            return ResponseEntity.ok(new ApiResponse("Image updated successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Image update failed: " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<ApiResponse> deleteImage(@PathVariable("imageId") long imageId) {
        try {
            imageService.deleteImageById(imageId);
            return ResponseEntity.ok(new ApiResponse("Image deleted successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Failed to delete image: " + e.getMessage(), null));
        }
    }
}