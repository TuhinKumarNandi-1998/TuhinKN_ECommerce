package com.tuhinK.eCommerce.auth.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LoginRequest {

    @NotBlank(message = "Invalid credentials")
    private String email;

    @NotBlank(message = "Invalid credentials")
    private String password;
}
