package com.tuhinK.eCommerce.product.services;

import com.tuhinK.eCommerce.commons.exceptions.ResourceNotFoundException;
import com.tuhinK.eCommerce.product.dtos.ImageDto;
import com.tuhinK.eCommerce.product.models.Image;
import com.tuhinK.eCommerce.product.models.Product;
import com.tuhinK.eCommerce.product.repositories.ImageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceImpTest {

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ImageServiceImp imageService;

    private Image image;
    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Laptop");

        image = new Image();
        image.setId(1L);
        image.setFileName("laptop.jpg");
        image.setFileType("image/jpeg");
        image.setProduct(product);
    }

    @Nested
    @DisplayName("getImageById")
    class GetImageById {

        @Test
        @DisplayName("should return image when found")
        void getImageById_found() {
            when(imageRepository.findById(1L)).thenReturn(Optional.of(image));

            Image result = imageService.getImageById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getFileName()).isEqualTo("laptop.jpg");
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when not found")
        void getImageById_notFound() {
            when(imageRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> imageService.getImageById(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Image not found");
        }
    }

    @Nested
    @DisplayName("deleteImageById")
    class DeleteImageById {

        @Test
        @DisplayName("should delete image when found")
        void deleteImageById_found() {
            when(imageRepository.findById(1L)).thenReturn(Optional.of(image));

            imageService.deleteImageById(1L);

            verify(imageRepository).delete(image);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when not found")
        void deleteImageById_notFound() {
            when(imageRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> imageService.deleteImageById(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Image not found");

            verify(imageRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("updateImage")
    class UpdateImage {

        @Test
        @DisplayName("should update image file data successfully")
        void updateImage_success() {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "updated.png", "image/png", "updated-content".getBytes());

            when(imageRepository.findById(1L)).thenReturn(Optional.of(image));
            when(imageRepository.save(any(Image.class))).thenReturn(image);

            imageService.updateImage(file, 1L);

            verify(imageRepository).save(argThat(img ->
                    "updated.png".equals(img.getFileName()) &&
                            "image/png".equals(img.getFileType())
            ));
        }
    }
}
