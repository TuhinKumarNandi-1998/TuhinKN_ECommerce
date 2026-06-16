package com.tuhinK.eCommerce.cartandcheckout.dtos;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Accessors(chain = true)
public class CartRequestDto {
    private Long cartId;
    private Set<CartItemRequestDto> items;
    private BigDecimal totalAmount;
}
