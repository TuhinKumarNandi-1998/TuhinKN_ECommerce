package com.tuhinK.eCommerce.cartandcheckout.dtos;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AmountDto {
    private BigDecimal value;
    private String currency;
}
