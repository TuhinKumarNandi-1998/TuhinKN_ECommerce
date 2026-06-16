package com.tuhinK.eCommerce.order.controllers;

import com.tuhinK.eCommerce.commons.exceptions.ResourceNotFoundException;
import com.tuhinK.eCommerce.order.dtos.OrderDto;
import com.tuhinK.eCommerce.order.models.Order;
import com.tuhinK.eCommerce.order.services.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    private Order order;
    private OrderDto orderDto;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setOrderId(1L);

        orderDto = new OrderDto();
        orderDto.setId(1L);
        orderDto.setTotalAmount(new BigDecimal("999.99"));
    }

    @Nested
    @DisplayName("POST /api/v1/orders/order")
    class CreateOrder {

        @Test
        @DisplayName("should create order and return 200 with OrderDto")
        void createOrder_success() throws Exception {
            when(orderService.placeOrder(1L)).thenReturn(order);
            when(orderService.convertToDto(any(Order.class))).thenReturn(orderDto);

            mockMvc.perform(post("/api/v1/orders/order")
                            .param("userId", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Order created successfully!"))
                    .andExpect(jsonPath("$.data.id").value(1));
        }

        @Test
        @DisplayName("should handle exception and return 500 status code")
        void createOrder_exception() throws Exception {
            when(orderService.placeOrder(1L)).thenThrow(new RuntimeException("Cart is empty"));

            mockMvc.perform(post("/api/v1/orders/order")
                            .param("userId", "1"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value("Error Occurred"))
                    .andExpect(jsonPath("$.data").value("Cart is empty"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/orders/{orderId}/order")
    class GetOrderById {

        @Test
        @DisplayName("should fetch order successfully and return 200")
        void getOrderById_success() throws Exception {
            when(orderService.getOrder(1L)).thenReturn(orderDto);

            mockMvc.perform(get("/api/v1/orders/1/order"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Order fetched successfully!"))
                    .andExpect(jsonPath("$.data.id").value(1L));
        }

        @Test
        @DisplayName("should return 404 when order does not exist")
        void getOrderById_notFound() throws Exception {
            when(orderService.getOrder(99L)).thenThrow(new ResourceNotFoundException("Order not found"));

            mockMvc.perform(get("/api/v1/orders/99/order"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Oops!"))
                    .andExpect(jsonPath("$.data").value("Order not found"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/orders/{userId}/orders")
    class GetOrdersByUserId {

        @Test
        @DisplayName("should fetch orders by user id successfully and return 200")
        void getOrdersByUserId_success() throws Exception {
            when(orderService.getOrdersByUserId(1L)).thenReturn(List.of(orderDto));

            mockMvc.perform(get("/api/v1/orders/1/orders"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Order fetched successfully!"))
                    .andExpect(jsonPath("$.data[0].id").value(1L));
        }

        @Test
        @DisplayName("should return 404 when user not found or error thrown")
        void getOrdersByUserId_notFound() throws Exception {
            when(orderService.getOrdersByUserId(99L)).thenThrow(new ResourceNotFoundException("User not found"));

            mockMvc.perform(get("/api/v1/orders/99/orders"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Oops!"))
                    .andExpect(jsonPath("$.data").value("User not found"));
        }
    }
}
