package com.tuhinK.eCommerce.user.dtos;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserPasswordResetDto {
    private String email;
    private String enterPassword;
    private String confirmPassword;
}
