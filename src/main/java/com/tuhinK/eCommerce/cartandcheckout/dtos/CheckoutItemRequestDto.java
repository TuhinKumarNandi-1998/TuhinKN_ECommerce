package com.tuhinK.eCommerce.cartandcheckout.dtos;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CheckoutItemRequestDto {
    private String productName;
    private int  quantity;
    private double price;
    private String productId;
    private int userId;
    private String currency;
    private String orderId;
}
