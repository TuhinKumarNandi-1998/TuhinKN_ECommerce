package com.tuhinK.eCommerce.payment.dtos;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain=true)
public class PaymentLineItemDto {
    private String name;
    private String description;
    @jakarta.validation.constraints.Positive
    private int quantity;
    private BigDecimal unitPrice;
    private String currency;
}
