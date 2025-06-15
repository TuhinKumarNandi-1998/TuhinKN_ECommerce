package com.tuhinK.eCommerce.user.dtos;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UpdateUserRequest {
    private String firstName;
    private String middleName;
    private String lastName;
}
