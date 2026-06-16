package com.tuhinK.eCommerce.payment.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class InitiatePaymentRequest {

    @NotBlank(message = "Idempotency key is mandatory")
    private String idempotencyKey;

    @NotBlank(message = "Order reference is mandatory")
    private String orderReferenceId;

    private List<PaymentLineItemDto> lineItems;

    @NotNull(message = "Customer context is mandatory for fraud verification")
    @Valid
    private CustomerContextDto customer;

    @NotBlank(message = "Success redirect URL is mandatory")
    private String successUrl;

    @NotBlank(message = "Cancel redirect URL is mandatory")
    private String cancelUrl;

    // Opaque metadata bucket for dynamic gateway rules or auditing
    private Map<String, String> metadata;
}
