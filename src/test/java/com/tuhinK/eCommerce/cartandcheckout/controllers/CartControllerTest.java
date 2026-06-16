package com.tuhinK.eCommerce.cartandcheckout.controllers;

import com.tuhinK.eCommerce.cartandcheckout.models.Cart;
import com.tuhinK.eCommerce.cartandcheckout.services.CartService;
import com.tuhinK.eCommerce.commons.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
@AutoConfigureMockMvc(addFilters = false)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartService cartService;

    private Cart cart;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        cart.setId(10L);
        cart.setTotalAmount(new BigDecimal("150.00"));
    }

    @Nested
    @DisplayName("GET /api/v1/carts/{cartId}/my-cart")
    class GetCart {

        @Test
        @DisplayName("should return cart when found")
        void returnCart() throws Exception {
            when(cartService.getCart(10L)).thenReturn(cart);

            mockMvc.perform(get("/api/v1/carts/10/my-cart")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Success"))
                    .andExpect(jsonPath("$.data.id").value(10));
        }

        @Test
        @DisplayName("should return 404 when cart not found")
        void notFound() throws Exception {
            when(cartService.getCart(99L)).thenThrow(new ResourceNotFoundException("Cart not found"));

            mockMvc.perform(get("/api/v1/carts/99/my-cart")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Cart not found"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/carts/{cartId}/clear-cart")
    class ClearCart {

        @Test
        @DisplayName("should clear cart successfully")
        void success() throws Exception {
            mockMvc.perform(delete("/api/v1/carts/10/clear-cart")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Clear Cart Success!"));
        }

        @Test
        @DisplayName("should return 404 when cart not found")
        void notFound() throws Exception {
            doThrow(new ResourceNotFoundException("Cart not found")).when(cartService).clearCart(99L);

            mockMvc.perform(delete("/api/v1/carts/99/clear-cart")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Cart not found"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/carts/{cartId}/cart/total-price")
    class GetTotalPrice {

        @Test
        @DisplayName("should return total price")
        void returnPrice() throws Exception {
            when(cartService.getTotalPrice(10L)).thenReturn(new BigDecimal("150.00"));

            mockMvc.perform(get("/api/v1/carts/10/cart/total-price")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Total Price"))
                    .andExpect(jsonPath("$.data").value(150.00));
        }
    }
}
