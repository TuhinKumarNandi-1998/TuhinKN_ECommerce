package com.tuhinK.eCommerce.cartandcheckout.services;

import com.tuhinK.eCommerce.cartandcheckout.models.Cart;
import com.tuhinK.eCommerce.cartandcheckout.models.CartItem;
import com.tuhinK.eCommerce.cartandcheckout.repositories.CartItemRepository;
import com.tuhinK.eCommerce.cartandcheckout.repositories.CartRepository;
import com.tuhinK.eCommerce.product.exceptions.ProductNotFoundException;
import com.tuhinK.eCommerce.product.models.Product;
import com.tuhinK.eCommerce.product.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartItemServiceImpTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductService productService;

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartItemServiceImp cartItemService;

    private Cart cart;
    private Product product;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        cart.setId(10L);
        cart.setCartItems(new HashSet<>());

        product = new Product();
        product.setId(100L);
        product.setPrice(new BigDecimal("50.00"));

        cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cartItem.setUnitPrice(new BigDecimal("50.00"));
        cartItem.setTotalPrice();
    }

    @Nested
    @DisplayName("addCartItem")
    class AddCartItem {

        @Test
        @DisplayName("should add new item to cart")
        void addNewItem() {
            when(cartService.getCart(10L)).thenReturn(cart);
            when(productService.getProductById(100L)).thenReturn(product);

            cartItemService.addCartItem(10L, 100L, 2);

            assertThat(cart.getCartItems()).hasSize(1);
            verify(cartItemRepository, times(1)).save(any(CartItem.class));
            verify(cartRepository, times(1)).save(cart);
        }

        @Test
        @DisplayName("should increment quantity if item already exists")
        void incrementExistingItem() {
            cart.addItem(cartItem);
            when(cartService.getCart(10L)).thenReturn(cart);
            when(productService.getProductById(100L)).thenReturn(product);

            cartItemService.addCartItem(10L, 100L, 3);

            assertThat(cartItem.getQuantity()).isEqualTo(5);
            verify(cartItemRepository, times(1)).save(cartItem);
            verify(cartRepository, times(1)).save(cart);
        }
    }

    @Nested
    @DisplayName("removeCartItem")
    class RemoveCartItem {

        @Test
        @DisplayName("should remove item from cart")
        void removeCartItem() {
            cart.addItem(cartItem);
            when(cartService.getCart(10L)).thenReturn(cart);
            when(productService.getProductById(100L)).thenReturn(product);

            cartItemService.removeCartItem(10L, 100L);

            assertThat(cart.getCartItems()).isEmpty();
            verify(cartItemRepository, times(1)).delete(cartItem);
            verify(cartRepository, times(1)).save(cart);
        }
    }

    @Nested
    @DisplayName("updateItemQuantity")
    class UpdateItemQuantity {

        @Test
        @DisplayName("should update quantity")
        void updateQuantity() {
            cart.addItem(cartItem);
            when(cartService.getCart(10L)).thenReturn(cart);

            cartItemService.updateItemQuantity(10L, 100L, 1);

            assertThat(cartItem.getQuantity()).isEqualTo(3);
            verify(cartItemRepository, times(1)).save(cartItem);
            verify(cartRepository, times(1)).save(cart);
        }

        @Test
        @DisplayName("should throw exception when item not found")
        void itemNotFound() {
            when(cartService.getCart(10L)).thenReturn(cart);

            assertThatThrownBy(() -> cartItemService.updateItemQuantity(10L, 100L, 1))
                    .isInstanceOf(ProductNotFoundException.class)
                    .hasMessage("Item not found");
        }
    }

    @Nested
    @DisplayName("getCartItem")
    class GetCartItem {

        @Test
        @DisplayName("should return cart item")
        void returnCartItem() {
            cart.addItem(cartItem);
            when(cartService.getCart(10L)).thenReturn(cart);

            CartItem result = cartItemService.getCartItem(10L, 100L);
            assertThat(result).isEqualTo(cartItem);
        }
    }
}
