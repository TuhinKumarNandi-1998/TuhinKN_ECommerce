package com.tuhinK.eCommerce.payment.repository;

import com.tuhinK.eCommerce.payment.models.PaymentDetails;
import com.tuhinK.eCommerce.payment.models.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @BeforeEach
    void setUp() {
        paymentRepository.deleteAll();

        PaymentDetails paymentDetails = new PaymentDetails()
                .setOrderId("ORDER-123")
                .setPaymentId("PAY-456")
                .setPaymentLink("https://checkout.stripe.com/pay/cs_test_a1b2")
                .setStatus(PaymentStatus.INITIATED);

        PaymentDetails savedPaymentDetails = paymentRepository.save(paymentDetails);
    }

    @Test
    @DisplayName("should find payment details by order ID")
    void findByOrderId_success() {
        Optional<PaymentDetails> result = paymentRepository.findByOrderId("ORDER-123");

        assertThat(result).isPresent();
        assertThat(result.get().getPaymentId()).isEqualTo("PAY-456");
        assertThat(result.get().getPaymentLink()).isEqualTo("https://checkout.stripe.com/pay/cs_test_a1b2");
        assertThat(result.get().getStatus()).isEqualTo(PaymentStatus.INITIATED);
    }

    @Test
    @DisplayName("should return empty optional when order ID not found")
    void findByOrderId_notFound() {
        Optional<PaymentDetails> result = paymentRepository.findByOrderId("ORDER-999");
        assertThat(result).isEmpty();
    }
}
