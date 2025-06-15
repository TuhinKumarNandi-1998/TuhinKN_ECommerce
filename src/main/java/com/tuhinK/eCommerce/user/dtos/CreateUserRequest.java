package com.tuhinK.eCommerce.user.dtos;

import com.tuhinK.eCommerce.user.models.Role;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Collection;

@Data
@Accessors(chain = true)
public class CreateUserRequest {
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String password;
    private Collection<Role> userRole;
}
