package com.tuhinK.eCommerce.user.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuhinK.eCommerce.commons.exceptions.AlreadyExistException;
import com.tuhinK.eCommerce.commons.exceptions.ResourceNotFoundException;
import com.tuhinK.eCommerce.user.dtos.CreateUserRequest;
import com.tuhinK.eCommerce.user.dtos.UpdateUserRequest;
import com.tuhinK.eCommerce.user.dtos.UserDto;
import com.tuhinK.eCommerce.user.dtos.UserPasswordResetDto;
import com.tuhinK.eCommerce.user.models.User;
import com.tuhinK.eCommerce.user.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User()
                .setFirstName("John")
                .setMiddleName("M")
                .setLastName("Doe")
                .setEmail("john@test.com")
                .setPassword("encodedPassword");
        user.setId(1L);

        userDto = new UserDto()
                .setId(1L)
                .setFirstName("John")
                .setLastName("Doe")
                .setEmail("john@test.com");
    }

    @Nested
    @DisplayName("GET /api/v1/users/{userId}/user")
    class GetUserById {

        @Test
        @DisplayName("should return user with 200 when found")
        void getUserById_success() throws Exception {
            when(userService.getUserById(1L)).thenReturn(user);
            when(userService.convertUserToDto(user)).thenReturn(userDto);

            mockMvc.perform(get("/api/v1/users/1/user"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("User fetched successfully"))
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.firstName").value("John"))
                    .andExpect(jsonPath("$.data.email").value("john@test.com"));
        }

        @Test
        @DisplayName("should return 404 when user not found")
        void getUserById_notFound() throws Exception {
            when(userService.getUserById(99L)).thenThrow(new ResourceNotFoundException("User not found"));

            mockMvc.perform(get("/api/v1/users/99/user"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("User not found"))
                    .andExpect(jsonPath("$.data").isEmpty());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/users/add")
    class CreateUser {

        @Test
        @DisplayName("should create user with 200 on success")
        void createUser_success() throws Exception {
            CreateUserRequest request = new CreateUserRequest()
                    .setFirstName("Jane")
                    .setMiddleName("A")
                    .setLastName("Smith")
                    .setEmail("jane@test.com")
                    .setPassword("password123");

            User createdUser = new User()
                    .setFirstName("Jane")
                    .setLastName("Smith")
                    .setEmail("jane@test.com");
            createdUser.setId(2L);

            UserDto createdDto = new UserDto()
                    .setId(2L)
                    .setFirstName("Jane")
                    .setLastName("Smith")
                    .setEmail("jane@test.com");

            when(userService.createUser(any(CreateUserRequest.class))).thenReturn(createdUser);
            when(userService.convertUserToDto(createdUser)).thenReturn(createdDto);

            mockMvc.perform(post("/api/v1/users/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("User created successfully"))
                    .andExpect(jsonPath("$.data.id").value(2))
                    .andExpect(jsonPath("$.data.firstName").value("Jane"))
                    .andExpect(jsonPath("$.data.email").value("jane@test.com"));
        }

        @Test
        @DisplayName("should return 409 when email already exists")
        void createUser_conflict() throws Exception {
            CreateUserRequest request = new CreateUserRequest()
                    .setFirstName("John")
                    .setEmail("john@test.com")
                    .setPassword("password123");

            when(userService.createUser(any(CreateUserRequest.class)))
                    .thenThrow(new AlreadyExistException("Oops! john@test.com already exists"));

            mockMvc.perform(post("/api/v1/users/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.data").isEmpty());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/users/{userId}/update")
    class UpdateUser {

        @Test
        @DisplayName("should update user with 200 on success")
        void updateUser_success() throws Exception {
            UpdateUserRequest request = new UpdateUserRequest()
                    .setFirstName("Updated")
                    .setMiddleName("X")
                    .setLastName("Name");

            User updatedUser = new User()
                    .setFirstName("Updated")
                    .setMiddleName("X")
                    .setLastName("Name")
                    .setEmail("john@test.com");
            updatedUser.setId(1L);

            UserDto updatedDto = new UserDto()
                    .setId(1L)
                    .setFirstName("Updated")
                    .setLastName("Name")
                    .setEmail("john@test.com");

            when(userService.updateUser(any(UpdateUserRequest.class), eq(1L))).thenReturn(updatedUser);
            when(userService.convertUserToDto(updatedUser)).thenReturn(updatedDto);

            mockMvc.perform(put("/api/v1/users/1/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("User is updated"))
                    .andExpect(jsonPath("$.data.firstName").value("Updated"))
                    .andExpect(jsonPath("$.data.lastName").value("Name"));
        }

        @Test
        @DisplayName("should return 404 when user not found")
        void updateUser_notFound() throws Exception {
            UpdateUserRequest request = new UpdateUserRequest()
                    .setFirstName("Updated");

            when(userService.updateUser(any(UpdateUserRequest.class), eq(99L)))
                    .thenThrow(new ResourceNotFoundException("User Not Found"));

            mockMvc.perform(put("/api/v1/users/99/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("User Not Found"))
                    .andExpect(jsonPath("$.data").isEmpty());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/users/{userId}/delete")
    class DeleteUser {

        @Test
        @DisplayName("should delete user with 200 on success")
        void deleteUser_success() throws Exception {
            doNothing().when(userService).deleteUser(1L);

            mockMvc.perform(delete("/api/v1/users/1/delete"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("User is deleted successfully"));
        }

        @Test
        @DisplayName("should return 404 when user not found")
        void deleteUser_notFound() throws Exception {
            doThrow(new ResourceNotFoundException("User Not Found"))
                    .when(userService).deleteUser(99L);

            mockMvc.perform(delete("/api/v1/users/99/delete"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("User Not Found"))
                    .andExpect(jsonPath("$.data").isEmpty());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/users/reset-password")
    class ResetPassword {

        @Test
        @DisplayName("should reset password with 200 on success")
        void resetPassword_success() throws Exception {
            UserPasswordResetDto resetDto = new UserPasswordResetDto()
                    .setEmail("john@test.com")
                    .setEnterPassword("newPassword")
                    .setConfirmPassword("newPassword");

            doNothing().when(userService).resetPassword(any(UserPasswordResetDto.class));

            mockMvc.perform(post("/api/v1/users/reset-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(resetDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Password reset sucessful"));
        }

        @Test
        @DisplayName("should return 404 when user email not found")
        void resetPassword_userNotFound() throws Exception {
            UserPasswordResetDto resetDto = new UserPasswordResetDto()
                    .setEmail("unknown@test.com")
                    .setEnterPassword("newPassword")
                    .setConfirmPassword("newPassword");

            doThrow(new ResourceNotFoundException("User not found"))
                    .when(userService).resetPassword(any(UserPasswordResetDto.class));

            mockMvc.perform(post("/api/v1/users/reset-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(resetDto)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("User not found"))
                    .andExpect(jsonPath("$.data").isEmpty());
        }
    }
}
