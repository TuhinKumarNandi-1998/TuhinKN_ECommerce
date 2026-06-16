package com.tuhinK.eCommerce.cartandcheckout.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckoutSessionResponseDto {
    private String orderId;
    private String checkoutSessionId;
    private PaymentFlowType flowType;
    private String gatewayProvider;
    private String checkoutUrl;
    private String cancelUrl;
    private AmountDto totalAmount;

    public enum PaymentFlowType {
        REDIRECT,  // Frontend should navigate to checkoutUrl
        SDK        // Frontend should open the SDK modal using clientSecret/publicKey
    }
}
