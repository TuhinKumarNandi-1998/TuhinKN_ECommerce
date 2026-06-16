package com.tuhinK.eCommerce.user.services;

import com.tuhinK.eCommerce.user.dtos.CreateUserRequest;
import com.tuhinK.eCommerce.user.dtos.UpdateUserRequest;
import com.tuhinK.eCommerce.user.dtos.UserDto;
import com.tuhinK.eCommerce.user.dtos.UserPasswordResetDto;
import com.tuhinK.eCommerce.user.models.User;

public interface UserService {

    User getUserById(Long userId);

    User createUser(CreateUserRequest request);

    User updateUser(UpdateUserRequest request, Long userId);

    void deleteUser(Long userId);

    UserDto convertUserToDto(User user);

    User getAuthenticatedUser();

    void resetPassword(UserPasswordResetDto userPasswordResetDto);
}
