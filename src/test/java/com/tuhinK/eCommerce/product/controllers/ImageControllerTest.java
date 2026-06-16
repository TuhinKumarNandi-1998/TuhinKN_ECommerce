package com.tuhinK.eCommerce.product.controllers;

import com.tuhinK.eCommerce.commons.exceptions.ResourceNotFoundException;
import com.tuhinK.eCommerce.product.dtos.ImageDto;
import com.tuhinK.eCommerce.product.models.Image;
import com.tuhinK.eCommerce.product.services.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.rowset.serial.SerialBlob;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ImageController.class)
@AutoConfigureMockMvc(addFilters = false)
class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ImageService imageService;

    private Image image;

    @BeforeEach
    void setUp() throws Exception {
        image = new Image();
        image.setId(1L);
        image.setFileName("test.jpg");
        image.setFileType("image/jpeg");
        image.setImage(new SerialBlob("test-image-data".getBytes()));
    }

    @Nested
    @DisplayName("POST /api/v1/images/upload")
    class UploadImages {

        @Test
        @DisplayName("should upload images successfully with 200")
        void uploadImages_success() throws Exception {
            MockMultipartFile file1 = new MockMultipartFile(
                    "files", "image1.jpg", "image/jpeg", "img-data-1".getBytes());
            MockMultipartFile file2 = new MockMultipartFile(
                    "files", "image2.png", "image/png", "img-data-2".getBytes());

            ImageDto dto1 = new ImageDto().setId(1L).setFileName("image1.jpg").setDownloadUrl("http://localhost/api/v1/images/1");
            ImageDto dto2 = new ImageDto().setId(2L).setFileName("image2.png").setDownloadUrl("http://localhost/api/v1/images/2");

            when(imageService.saveImages(eq(1L), anyList())).thenReturn(List.of(dto1, dto2));

            mockMvc.perform(multipart("/api/v1/images/upload")
                            .file(file1)
                            .file(file2)
                            .param("productId", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Upload success"))
                    .andExpect(jsonPath("$.data[0].fileName").value("image1.jpg"))
                    .andExpect(jsonPath("$.data[1].fileName").value("image2.png"));
        }

        @Test
        @DisplayName("should return 500 on upload failure")
        void uploadImages_failure() throws Exception {
            MockMultipartFile file = new MockMultipartFile(
                    "files", "bad.jpg", "image/jpeg", "data".getBytes());

            when(imageService.saveImages(eq(1L), anyList()))
                    .thenThrow(new RuntimeException("IO error"));

            mockMvc.perform(multipart("/api/v1/images/upload")
                            .file(file)
                            .param("productId", "1"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value("Upload failed"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/images/download/{imageId}")
    class DownloadImage {

        @Test
        @DisplayName("should download image with correct content type")
        void downloadImage_success() throws Exception {
            when(imageService.getImageById(1L)).thenReturn(image);

            mockMvc.perform(get("/api/v1/images/download/1"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Content-Disposition", "attachment; filename=\"test.jpg\""));
        }

        @Test
        @DisplayName("should return 404 when image not found")
        void downloadImage_notFound() throws Exception {
            when(imageService.getImageById(99L)).thenThrow(new ResourceNotFoundException("Image not found 99"));

            mockMvc.perform(get("/api/v1/images/download/99"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("should return 404 when image is null")
        void downloadImage_nullImage() throws Exception {
            Image nullImage = new Image();
            nullImage.setId(2L);
            nullImage.setImage(null);

            when(imageService.getImageById(2L)).thenReturn(nullImage);

            mockMvc.perform(get("/api/v1/images/download/2"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/images/{imageId}")
    class UpdateImage {

        @Test
        @DisplayName("should update image successfully with 200")
        void updateImage_success() throws Exception {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "updated.jpg", "image/jpeg", "updated-data".getBytes());

            when(imageService.getImageById(1L)).thenReturn(image);
            doNothing().when(imageService).updateImage(any(), eq(1L));

            mockMvc.perform(multipart("/api/v1/images/1")
                            .file(file)
                            .with(request -> {
                                request.setMethod("PUT");
                                return request;
                            }))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Image updated successfully"));
        }

        @Test
        @DisplayName("should return 404 when image not found for update")
        void updateImage_notFound() throws Exception {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "updated.jpg", "image/jpeg", "data".getBytes());

            when(imageService.getImageById(99L)).thenThrow(new ResourceNotFoundException("Image not found 99"));

            mockMvc.perform(multipart("/api/v1/images/99")
                            .file(file)
                            .with(request -> {
                                request.setMethod("PUT");
                                return request;
                            }))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Image not found 99"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/images/{imageId}")
    class DeleteImage {

        @Test
        @DisplayName("should delete image successfully with 200")
        void deleteImage_success() throws Exception {
            doNothing().when(imageService).deleteImageById(1L);

            mockMvc.perform(delete("/api/v1/images/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Image deleted successfully"));
        }

        @Test
        @DisplayName("should return 404 when image not found for delete")
        void deleteImage_notFound() throws Exception {
            doThrow(new ResourceNotFoundException("Image not found 99"))
                    .when(imageService).deleteImageById(99L);

            mockMvc.perform(delete("/api/v1/images/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Image not found 99"));
        }
    }
}
