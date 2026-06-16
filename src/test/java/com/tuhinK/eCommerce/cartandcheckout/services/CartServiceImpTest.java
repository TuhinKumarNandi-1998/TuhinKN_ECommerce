package com.tuhinK.eCommerce.cartandcheckout.services;

import com.tuhinK.eCommerce.cartandcheckout.models.Cart;
import com.tuhinK.eCommerce.cartandcheckout.repositories.CartItemRepository;
import com.tuhinK.eCommerce.cartandcheckout.repositories.CartRepository;
import com.tuhinK.eCommerce.commons.exceptions.ResourceNotFoundException;
import com.tuhinK.eCommerce.user.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImpTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private CartServiceImp cartService;

    private User user;
    private Cart cart;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        cart = new Cart();
        cart.setId(10L);
        cart.setUser(user);
        cart.setTotalAmount(new BigDecimal("150.00"));
    }

    @Nested
    @DisplayName("initializeNewCart")
    class InitializeNewCart {

        @Test
        @DisplayName("should return existing cart if found")
        void returnExistingCart() {
            when(cartRepository.findByUserId(1L)).thenReturn(cart);
            Cart result = cartService.initializeNewCart(user);
            assertThat(result).isEqualTo(cart);
            verify(cartRepository, never()).save(any());
        }

        @Test
        @DisplayName("should create new cart if not found")
        void createNewCart() {
            when(cartRepository.findByUserId(1L)).thenReturn(null);
            when(cartRepository.save(any(Cart.class))).thenReturn(cart);

            Cart result = cartService.initializeNewCart(user);

            assertThat(result).isNotNull();
            verify(cartRepository, times(1)).save(any(Cart.class));
        }
    }

    @Nested
    @DisplayName("getCartByUserId")
    class GetCartByUserId {
        @Test
        @DisplayName("should return cart")
        void returnCart() {
            when(cartRepository.findByUserId(1L)).thenReturn(cart);
            assertThat(cartService.getCartByUserId(1L)).isEqualTo(cart);
        }
    }

    @Nested
    @DisplayName("getCart")
    class GetCart {
        @Test
        @DisplayName("should return cart when exists")
        void returnCartWhenExists() {
            when(cartRepository.findById(10L)).thenReturn(Optional.of(cart));
            assertThat(cartService.getCart(10L)).isEqualTo(cart);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when not exists")
        void throwExceptionWhenNotExists() {
            when(cartRepository.findById(10L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> cartService.getCart(10L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Cart not found");
        }
    }

    @Nested
    @DisplayName("clearCart")
    class ClearCart {
        @Test
        @DisplayName("should clear items and delete cart")
        void clearCartAndItems() {
            when(cartRepository.findById(10L)).thenReturn(Optional.of(cart));

            cartService.clearCart(10L);

            verify(cartItemRepository, times(1)).deleteAllByCartId(10L);
            verify(cartRepository, times(1)).deleteById(10L);
            assertThat(cart.getCartItems()).isEmpty();
        }
    }

    @Nested
    @DisplayName("getTotalPrice")
    class GetTotalPrice {
        @Test
        @DisplayName("should return total price of cart")
        void returnTotalPrice() {
            when(cartRepository.findById(10L)).thenReturn(Optional.of(cart));
            assertThat(cartService.getTotalPrice(10L)).isEqualTo(new BigDecimal("150.00"));
        }
    }
}
