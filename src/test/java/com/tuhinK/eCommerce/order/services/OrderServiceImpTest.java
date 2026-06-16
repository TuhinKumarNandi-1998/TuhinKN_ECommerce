package com.tuhinK.eCommerce.order.services;

import com.tuhinK.eCommerce.cartandcheckout.models.Cart;
import com.tuhinK.eCommerce.cartandcheckout.models.CartItem;
import com.tuhinK.eCommerce.cartandcheckout.services.CartService;
import com.tuhinK.eCommerce.commons.exceptions.ResourceNotFoundException;
import com.tuhinK.eCommerce.order.dtos.OrderDto;
import com.tuhinK.eCommerce.order.models.Order;
import com.tuhinK.eCommerce.order.models.OrderStatus;
import com.tuhinK.eCommerce.order.repositories.OrderRepository;
import com.tuhinK.eCommerce.product.models.Product;
import com.tuhinK.eCommerce.product.repositories.ProductRepository;
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
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImpTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartService cartService;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderServiceImp orderService;

    private User user;
    private Product product;
    private Cart cart;
    private CartItem cartItem;
    private Order order;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(new BigDecimal("999.99"));
        product.setInventory(10);

        cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cartItem.setUnitPrice(new BigDecimal("999.99"));

        cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setCartItems(Set.of(cartItem));

        order = new Order();
        order.setOrderId(1L);
        order.setUser(user);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDate.now());
    }

    @Nested
    @DisplayName("placeOrder")
    class PlaceOrder {

        @Test
        @DisplayName("should place order successfully and clear cart")
        void placeOrder_success() {
            when(cartService.getCartByUserId(1L)).thenReturn(cart);
            when(productRepository.save(any(Product.class))).thenReturn(product);
            when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
                Order o = inv.getArgument(0);
                o.setOrderId(100L);
                return o;
            });
            doNothing().when(cartService).clearCart(1L);

            Order savedOrder = orderService.placeOrder(1L);

            assertThat(savedOrder).isNotNull();
            assertThat(savedOrder.getOrderId()).isEqualTo(100L);
            assertThat(savedOrder.getOrderStatus()).isEqualTo(OrderStatus.PENDING);
            assertThat(savedOrder.getTotalAmount()).isEqualByComparingTo(new BigDecimal("1999.98"));

            // Verify inventory gets reduced
            assertThat(product.getInventory()).isEqualTo(8);

            verify(cartService).getCartByUserId(1L);
            verify(productRepository).save(any(Product.class));
            verify(orderRepository).save(any(Order.class));
            verify(cartService).clearCart(1L);
        }

        @Test
        @DisplayName("should throw error if cart not found")
        void placeOrder_cartNotFound() {
            when(cartService.getCartByUserId(99L)).thenThrow(new ResourceNotFoundException("Cart not found"));

            assertThatThrownBy(() -> orderService.placeOrder(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Cart not found");

            verify(orderRepository, never()).save(any(Order.class));
            verify(cartService, never()).clearCart(anyLong());
        }
    }

    @Nested
    @DisplayName("getOrder")
    class GetOrder {

        @Test
        @DisplayName("should return order DTO when found")
        void getOrder_found() {
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

            OrderDto result = orderService.getOrder(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getTotalAmount()).isEqualByComparingTo(new BigDecimal("1999.98"));
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when not found")
        void getOrder_notFound() {
            when(orderRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.getOrder(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Order not found");
        }
    }

    @Nested
    @DisplayName("getOrdersByUserId")
    class GetOrdersByUserId {

        @Test
        @DisplayName("should return list of mapped order dtos for user")
        void getOrdersByUserId_found() {
            when(orderRepository.findByUserId(1L)).thenReturn(List.of(order));

            List<OrderDto> result = orderService.getOrdersByUserId(1L);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("should return empty list when no orders mapped")
        void getOrdersByUserId_empty() {
            when(orderRepository.findByUserId(99L)).thenReturn(List.of());

            List<OrderDto> result = orderService.getOrdersByUserId(99L);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("convertToDto")
    class ConvertToDto {

        @Test
        @DisplayName("should convert Order to OrderDto")
        void convertToDto_success() {
            OrderDto dto = orderService.convertToDto(order);

            assertThat(dto).isNotNull();
            assertThat(dto.getId()).isEqualTo(order.getOrderId());
            assertThat(dto.getOrderStatus()).isEqualTo(order.getOrderStatus().toString());
            assertThat(dto.getTotalAmount()).isEqualByComparingTo(order.getTotalAmount());
        }
    }
}
