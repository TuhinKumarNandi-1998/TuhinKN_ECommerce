package com.tuhinK.eCommerce.payment.dtos;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CustomerContextDto {
    private String customerEmail;
    private String firstName;
    private String lastName;
}
