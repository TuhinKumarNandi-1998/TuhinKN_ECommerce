package com.tuhinK.eCommerce.auth.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
public class JwtResponse {
    private Long id;
    private String token;
}
