package com.tuhinK.eCommerce.payment.services;

import com.tuhinK.eCommerce.payment.dtos.InitiatePaymentRequest;
import com.tuhinK.eCommerce.payment.dtos.PaymentSessionResponse;
import com.tuhinK.eCommerce.payment.exception.PaymentGatewayException;

public interface PaymentService {
    PaymentSessionResponse createGatewaySession(InitiatePaymentRequest paymentRequest) throws PaymentGatewayException;
}
