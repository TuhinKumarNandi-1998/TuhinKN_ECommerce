package com.tuhinK.eCommerce.user.dtos;

import com.tuhinK.eCommerce.cartAndCheckout.dtos.CartRequestDto;
import com.tuhinK.eCommerce.order.dtos.OrderDto;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private List<OrderDto> orders;
    private CartRequestDto cart;
}
