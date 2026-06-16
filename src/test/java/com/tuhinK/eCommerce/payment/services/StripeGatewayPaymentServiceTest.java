package com.tuhinK.eCommerce.payment.services;

import com.stripe.exception.ApiConnectionException;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import com.stripe.param.checkout.SessionCreateParams;
import com.tuhinK.eCommerce.payment.dtos.InitiatePaymentRequest;
import com.tuhinK.eCommerce.payment.dtos.PaymentLineItemDto;
import com.tuhinK.eCommerce.payment.dtos.PaymentSessionResponse;
import com.tuhinK.eCommerce.payment.exception.PaymentGatewayException;
import com.tuhinK.eCommerce.payment.models.PaymentDetails;
import com.tuhinK.eCommerce.payment.repository.PaymentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StripeGatewayPaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private StripeGatewayPaymentService paymentService;

    private MockedStatic<Session> mockedSession;
    private InitiatePaymentRequest paymentRequest;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(paymentService, "secretKey", "sk_test_12345");

        PaymentLineItemDto lineItem = new PaymentLineItemDto()
                .setName("Laptop")
                .setQuantity(1)
                .setUnitPrice(new BigDecimal("1500"))
                .setCurrency("usd");

        paymentRequest = InitiatePaymentRequest.builder()
                .idempotencyKey("idemp_123")
                .orderReferenceId("ORDER_123")
                .lineItems(List.of(lineItem))
                .successUrl("http://success")
                .cancelUrl("http://cancel")
                .build();

        mockedSession = mockStatic(Session.class);
    }

    @AfterEach
    void tearDown() {
        mockedSession.close();
    }

    @Test
    @DisplayName("should create gateway session and save payment details")
    void createGatewaySession_success() throws StripeException, PaymentGatewayException {
        Session mockStripeSession = mock(Session.class);
        when(mockStripeSession.getId()).thenReturn("cs_test_123");
        when(mockStripeSession.getUrl()).thenReturn("http://checkout.stripe.com/pay/cs_test_123");
        when(mockStripeSession.getCancelUrl()).thenReturn("http://cancel");
        when(mockStripeSession.getClientSecret()).thenReturn("pi_123_secret_456");

        mockedSession.when(() -> Session.create(any(SessionCreateParams.class), any(RequestOptions.class)))
                .thenReturn(mockStripeSession);

        PaymentSessionResponse response = paymentService.createGatewaySession(paymentRequest);

        assertThat(response).isNotNull();
        assertThat(response.getGatewayProvider()).isEqualTo("STRIPE");
        assertThat(response.getGatewaySessionId()).isEqualTo("cs_test_123");
        assertThat(response.getCheckoutUrl()).isEqualTo("http://checkout.stripe.com/pay/cs_test_123");
        assertThat(response.getAmount()).isEqualTo(new BigDecimal("1500"));
        assertThat(response.getCurrency()).isEqualTo("usd");

        verify(paymentRepository, times(1)).save(any(PaymentDetails.class));
    }

    @Test
    @DisplayName("should throw PaymentGatewayException when StripeException occurs")
    void createGatewaySession_failure() {
        mockedSession.when(() -> Session.create(any(SessionCreateParams.class), any(RequestOptions.class)))
                .thenThrow(new ApiConnectionException("Stripe API connection failed"));

        assertThatThrownBy(() -> paymentService.createGatewaySession(paymentRequest))
                .isInstanceOf(PaymentGatewayException.class)
                .hasMessageContaining("payment gateway engine failed to spin up financial gateway context");

        verify(paymentRepository, never()).save(any(PaymentDetails.class));
    }
}
