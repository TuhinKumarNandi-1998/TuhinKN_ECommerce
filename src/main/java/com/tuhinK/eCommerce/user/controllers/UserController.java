package com.tuhinK.eCommerce.user.controllers;

import com.tuhinK.eCommerce.commons.dtos.ApiResponse;
import com.tuhinK.eCommerce.commons.exceptions.AlreadyExistException;
import com.tuhinK.eCommerce.commons.exceptions.ResourceNotFoundException;
import com.tuhinK.eCommerce.user.dtos.CreateUserRequest;
import com.tuhinK.eCommerce.user.dtos.UpdateUserRequest;
import com.tuhinK.eCommerce.user.dtos.UserDto;
import com.tuhinK.eCommerce.user.dtos.UserPasswordResetDto;
import com.tuhinK.eCommerce.user.models.User;
import com.tuhinK.eCommerce.user.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("${api_prefix}/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}/user")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable("userId") Long userId) {
        try {
            User user = userService.getUserById(userId);
            UserDto userDto = userService.convertUserToDto(user);

            log.info("User with id {} fetched successfully", userId);

            return ResponseEntity.ok(new ApiResponse("User fetched successfully", userDto));
        } catch (ResourceNotFoundException exception) {
            log.info("User with id {} not found {} ",userId, exception.getMessage());
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(exception.getMessage(), null));
        }
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> createUser(@RequestBody CreateUserRequest request) {
        try {
            User user = userService.createUser(request);
            UserDto userDto = userService.convertUserToDto(user);
            return ResponseEntity.ok(new ApiResponse("User created successfully", userDto));
        } catch (AlreadyExistException e) {
            return ResponseEntity.status(CONFLICT).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping("{userId}/update")
    public ResponseEntity<ApiResponse> updateUser(@RequestBody UpdateUserRequest request, @PathVariable("userId") Long userId) {
        try {
            User user = userService.updateUser(request, userId);
            UserDto userDto = userService.convertUserToDto(user);

            log.info("User with id {} is updated", userId);

            return ResponseEntity.ok(new ApiResponse("User is updated", userDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @DeleteMapping("{userId}/delete")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable("userId") Long userId) {
        try {
            userService.deleteUser(userId);

            log.info("User with id {} is successfully deleted",userId);

            return ResponseEntity.ok(new ApiResponse("User is deleted successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody UserPasswordResetDto resetDto) {
        try {
            userService.resetPassword(resetDto);

            log.info("Password reset successful");

            return ResponseEntity.ok(new ApiResponse("Password reset sucessful", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }
}
