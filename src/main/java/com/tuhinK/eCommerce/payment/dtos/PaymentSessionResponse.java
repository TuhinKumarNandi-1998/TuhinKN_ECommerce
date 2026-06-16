package com.tuhinK.eCommerce.payment.dtos;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentSessionResponse {
    private String paymentTransactionId;
    private String gatewayProvider;
    private String gatewaySessionId;
    private String checkoutUrl;
    private String cancelUrl;
    private String clientSecret;
    private String publicKey;
    private BigDecimal amount;
    private String currency;
}
