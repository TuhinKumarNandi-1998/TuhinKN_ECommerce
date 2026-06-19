package com.tuhinK.eCommerce.user.services;

import com.tuhinK.eCommerce.commons.exceptions.AlreadyExistException;
import com.tuhinK.eCommerce.commons.exceptions.ResourceNotFoundException;
import com.tuhinK.eCommerce.user.dtos.CreateUserRequest;
import com.tuhinK.eCommerce.user.dtos.UpdateUserRequest;
import com.tuhinK.eCommerce.user.dtos.UserDto;
import com.tuhinK.eCommerce.user.dtos.UserPasswordResetDto;
import com.tuhinK.eCommerce.user.models.User;
import com.tuhinK.eCommerce.user.repositories.UserRepository;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ModelMapper modelMapper;

    @Autowired
    public UserServiceImp(UserRepository userRepository,
                          BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.modelMapper = new ModelMapper();
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public User createUser(CreateUserRequest request) {
        return Optional.of(request)
                .filter(user -> !userRepository.existsByEmail(request.getEmail()))
                .map(userRequest -> {
                    User user = new User()
                            .setEmail(request.getEmail())
                            .setPassword(bCryptPasswordEncoder.encode(request.getPassword()))
                            .setFirstName(request.getFirstName())
                            .setLastName(request.getLastName())
                            .setMiddleName(request.getMiddleName())
                            .setAllRoles(request.getUserRole());
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new AlreadyExistException("Oops! " + request.getEmail() + " already exists"));
    }

    @Override
    public User updateUser(UpdateUserRequest userRequest, Long userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    user.setFirstName(userRequest.getFirstName());
                    user.setMiddleName(userRequest.getMiddleName());
                    user.setLastName(userRequest.getLastName());

                    return userRepository.save(user);
                })
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.findById(userId)
                .ifPresentOrElse(userRepository::delete,
                        () -> { throw new ResourceNotFoundException("User Not Found"); }
                );
    }

    @Override
    public UserDto convertUserToDto(User user) {
        Hibernate.initialize(user.getAllRoles());
        Hibernate.initialize(user.getOrders());
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() ->  new ResourceNotFoundException("User not found"));
    }

    @Override
    public void resetPassword(UserPasswordResetDto userPasswordResetDto) {
        if (!userPasswordResetDto.getEnterPassword().equals(userPasswordResetDto.getConfirmPassword())) {
            throw new IllegalArgumentException("Password do not match");
        }

        User user = userRepository.findByEmail(userPasswordResetDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setPassword(bCryptPasswordEncoder.encode(userPasswordResetDto.getConfirmPassword()));

        userRepository.save(user);
    }
}
