package com.tuhinK.eCommerce.cartandcheckout.services;

import com.tuhinK.eCommerce.cartandcheckout.dtos.CheckoutItemRequestDto;
import com.tuhinK.eCommerce.cartandcheckout.dtos.CheckoutSessionResponseDto;
import com.tuhinK.eCommerce.cartandcheckout.exception.CheckoutException;
import com.tuhinK.eCommerce.order.models.Order;
import com.tuhinK.eCommerce.order.models.OrderStatus;
import com.tuhinK.eCommerce.order.repositories.OrderRepository;
import com.tuhinK.eCommerce.payment.dtos.InitiatePaymentRequest;
import com.tuhinK.eCommerce.payment.dtos.PaymentSessionResponse;
import com.tuhinK.eCommerce.payment.exception.PaymentGatewayException;
import com.tuhinK.eCommerce.payment.services.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceImpTest {

    @Mock
    private PaymentService paymentService;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private CheckoutServiceImp checkoutService;

    private CheckoutItemRequestDto checkoutItem;
    private Order order;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(checkoutService, "baseURL", "http://localhost:8081/");

        checkoutItem = new CheckoutItemRequestDto()
                .setOrderId("100")
                .setProductName("Laptop")
                .setQuantity(1)
                .setPrice(1500.0)
                .setCurrency("usd");

        order = new Order();
        order.setOrderId(100L);
        order.setOrderStatus(OrderStatus.PENDING);
    }

    @Nested
    @DisplayName("createSession")
    class CreateSession {

        @Test
        @DisplayName("should create session and update order status")
        void success() throws PaymentGatewayException {
            PaymentSessionResponse gatewayResponse = PaymentSessionResponse.builder()
                    .gatewaySessionId("session_123")
                    .gatewayProvider("stripe")
                    .checkoutUrl("http://checkout.url")
                    .cancelUrl("http://cancel.url")
                    .amount(new BigDecimal("1500"))
                    .currency("usd")
                    .build();

            when(paymentService.createGatewaySession(any(InitiatePaymentRequest.class)))
                    .thenReturn(gatewayResponse);
            when(orderRepository.findById(100L)).thenReturn(Optional.of(order));

            CheckoutSessionResponseDto response = checkoutService.createSession(List.of(checkoutItem));

            assertThat(response).isNotNull();
            assertThat(response.getOrderId()).isEqualTo("100");
            assertThat(response.getCheckoutSessionId()).isEqualTo("session_123");
            assertThat(response.getCheckoutUrl()).isEqualTo("http://checkout.url");
            assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PROCESSING);

            verify(orderRepository).save(order);
            verify(paymentService).createGatewaySession(any(InitiatePaymentRequest.class));
        }

        @Test
        @DisplayName("should throw CheckoutException on PaymentGatewayException")
        void gatewayError() throws PaymentGatewayException {
            when(paymentService.createGatewaySession(any(InitiatePaymentRequest.class)))
                    .thenThrow(new PaymentGatewayException("Gateway unavailable", null));

            assertThatThrownBy(() -> checkoutService.createSession(List.of(checkoutItem)))
                    .isInstanceOf(CheckoutException.class)
                    .hasMessageContaining("checkout failed : Gateway unavailable");
        }

        @Test
        @DisplayName("should throw CheckoutException if quantity is zero")
        void invalidQuantity() {
            checkoutItem.setQuantity(0);

            assertThatThrownBy(() -> checkoutService.createSession(List.of(checkoutItem)))
                    .isInstanceOf(CheckoutException.class)
                    .hasMessageContaining("Checkout item empty");
        }
    }
}
