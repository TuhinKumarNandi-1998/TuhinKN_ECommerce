package com.tuhinK.eCommerce.payment.repository;

import com.tuhinK.eCommerce.payment.models.PaymentDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentDetails, Long> {
    Optional<PaymentDetails> findByOrderId(String orderId);
}
