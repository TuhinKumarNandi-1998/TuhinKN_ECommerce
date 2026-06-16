package com.tuhinK.eCommerce.cartandcheckout.dtos;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CartItemRequestDto {
    private String productName;
    private int  quantity;
    private double price;
    private long productId;
    private int userId;
    private String currency;
    private long orderId;
}
