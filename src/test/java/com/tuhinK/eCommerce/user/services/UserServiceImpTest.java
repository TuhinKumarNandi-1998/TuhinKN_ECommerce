package com.tuhinK.eCommerce.user.services;

import com.tuhinK.eCommerce.commons.exceptions.AlreadyExistException;
import com.tuhinK.eCommerce.commons.exceptions.ResourceNotFoundException;
import com.tuhinK.eCommerce.user.dtos.CreateUserRequest;
import com.tuhinK.eCommerce.user.dtos.UpdateUserRequest;
import com.tuhinK.eCommerce.user.dtos.UserDto;
import com.tuhinK.eCommerce.user.dtos.UserPasswordResetDto;
import com.tuhinK.eCommerce.user.models.Role;
import com.tuhinK.eCommerce.user.models.User;
import com.tuhinK.eCommerce.user.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImpTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private UserServiceImp userService;

    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImp(userRepository, bCryptPasswordEncoder);

        role = new Role("ROLE_USER");
        role.setId(1L);

        user = new User()
                .setFirstName("John")
                .setMiddleName("M")
                .setLastName("Doe")
                .setEmail("john@test.com")
                .setPassword("encodedPassword")
                .setAllRoles(List.of(role));
        user.setId(1L);
    }

    @Nested
    @DisplayName("getUserById")
    class GetUserById {

        @Test
        @DisplayName("should return user when found")
        void getUserById_found() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            User result = userService.getUserById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getFirstName()).isEqualTo("John");
            assertThat(result.getEmail()).isEqualTo("john@test.com");
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when user not found")
        void getUserById_notFound() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getUserById(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("User not found");
        }
    }

    @Nested
    @DisplayName("createUser")
    class CreateUser {

        @Test
        @DisplayName("should create user successfully when email does not exist")
        void createUser_success() {
            CreateUserRequest request = new CreateUserRequest()
                    .setFirstName("Jane")
                    .setMiddleName("A")
                    .setLastName("Smith")
                    .setEmail("jane@test.com")
                    .setPassword("password123")
                    .setUserRole(List.of(role));

            when(userRepository.existsByEmail("jane@test.com")).thenReturn(false);
            when(bCryptPasswordEncoder.encode("password123")).thenReturn("encodedPass");
            when(userRepository.save(any(User.class))).thenAnswer(inv -> {
                User saved = inv.getArgument(0);
                saved.setId(2L);
                return saved;
            });

            User result = userService.createUser(request);

            assertThat(result).isNotNull();
            assertThat(result.getFirstName()).isEqualTo("Jane");
            assertThat(result.getLastName()).isEqualTo("Smith");
            assertThat(result.getEmail()).isEqualTo("jane@test.com");
            assertThat(result.getPassword()).isEqualTo("encodedPass");

            verify(userRepository).existsByEmail("jane@test.com");
            verify(bCryptPasswordEncoder).encode("password123");
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("should throw AlreadyExistException when email already exists")
        void createUser_emailAlreadyExists() {
            CreateUserRequest request = new CreateUserRequest()
                    .setFirstName("John")
                    .setEmail("john@test.com")
                    .setPassword("password123");

            when(userRepository.existsByEmail("john@test.com")).thenReturn(true);

            assertThatThrownBy(() -> userService.createUser(request))
                    .isInstanceOf(AlreadyExistException.class);

            verify(userRepository, never()).save(any(User.class));
            verify(bCryptPasswordEncoder, never()).encode(anyString());
        }

        @Test
        @DisplayName("should encode password before saving")
        void createUser_encodesPassword() {
            CreateUserRequest request = new CreateUserRequest()
                    .setFirstName("Test")
                    .setEmail("test@test.com")
                    .setPassword("rawPassword");

            when(userRepository.existsByEmail("test@test.com")).thenReturn(false);
            when(bCryptPasswordEncoder.encode("rawPassword")).thenReturn("$2a$encoded");
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            User result = userService.createUser(request);

            assertThat(result.getPassword()).isEqualTo("$2a$encoded");
            verify(bCryptPasswordEncoder).encode("rawPassword");
        }
    }

    @Nested
    @DisplayName("updateUser")
    class UpdateUser {

        @Test
        @DisplayName("should update user successfully when found")
        void updateUser_success() {
            UpdateUserRequest request = new UpdateUserRequest()
                    .setFirstName("Updated")
                    .setMiddleName("X")
                    .setLastName("Name");

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            User result = userService.updateUser(request, 1L);

            assertThat(result).isNotNull();
            assertThat(result.getFirstName()).isEqualTo("Updated");
            assertThat(result.getMiddleName()).isEqualTo("X");
            assertThat(result.getLastName()).isEqualTo("Name");

            verify(userRepository).findById(1L);
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when user not found")
        void updateUser_notFound() {
            UpdateUserRequest request = new UpdateUserRequest()
                    .setFirstName("Updated");

            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.updateUser(request, 99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("User Not Found");

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("should preserve email and password during update")
        void updateUser_preservesEmailAndPassword() {
            UpdateUserRequest request = new UpdateUserRequest()
                    .setFirstName("New")
                    .setMiddleName("M")
                    .setLastName("Name");

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            User result = userService.updateUser(request, 1L);

            assertThat(result.getEmail()).isEqualTo("john@test.com");
            assertThat(result.getPassword()).isEqualTo("encodedPassword");
        }
    }

    @Nested
    @DisplayName("deleteUser")
    class DeleteUser {

        @Test
        @DisplayName("should delete user when found")
        void deleteUser_success() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            userService.deleteUser(1L);

            verify(userRepository).delete(user);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when user not found")
        void deleteUser_notFound() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.deleteUser(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("User Not Found");

            verify(userRepository, never()).delete(any(User.class));
        }
    }

    @Nested
    @DisplayName("convertUserToDto")
    class ConvertUserToDto {

        @Test
        @DisplayName("should convert User to UserDto")
        void convertUserToDto_success() {
            user.setOrders(List.of());
            user.setAllRoles(List.of(role));

            UserDto dto = userService.convertUserToDto(user);

            assertThat(dto).isNotNull();
            assertThat(dto.getId()).isEqualTo(1L);
            assertThat(dto.getFirstName()).isEqualTo("John");
            assertThat(dto.getLastName()).isEqualTo("Doe");
            assertThat(dto.getEmail()).isEqualTo("john@test.com");
        }

        @Test
        @DisplayName("should handle user with null orders")
        void convertUserToDto_nullOrders() {
            user.setOrders(null);
            user.setAllRoles(List.of(role));

            UserDto dto = userService.convertUserToDto(user);

            assertThat(dto).isNotNull();
            assertThat(dto.getFirstName()).isEqualTo("John");
        }
    }

    @Nested
    @DisplayName("getAuthenticatedUser")
    class GetAuthenticatedUser {

        @Test
        @DisplayName("should return authenticated user when found")
        void getAuthenticatedUser_success() {
            Authentication authentication = mock(Authentication.class);
            SecurityContext securityContext = mock(SecurityContext.class);

            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("john@test.com");
            SecurityContextHolder.setContext(securityContext);

            when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(user));

            User result = userService.getAuthenticatedUser();

            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo("john@test.com");
            assertThat(result.getFirstName()).isEqualTo("John");

            verify(userRepository).findByEmail("john@test.com");
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when authenticated user not in DB")
        void getAuthenticatedUser_notFound() {
            Authentication authentication = mock(Authentication.class);
            SecurityContext securityContext = mock(SecurityContext.class);

            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("unknown@test.com");
            SecurityContextHolder.setContext(securityContext);

            when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getAuthenticatedUser())
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("User not found");
        }
    }

    @Nested
    @DisplayName("resetPassword")
    class ResetPassword {

        @Test
        @DisplayName("should reset password successfully when passwords match")
        void resetPassword_success() {
            UserPasswordResetDto resetDto = new UserPasswordResetDto()
                    .setEmail("john@test.com")
                    .setEnterPassword("newPassword")
                    .setConfirmPassword("newPassword");

            when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(user));
            when(bCryptPasswordEncoder.encode("newPassword")).thenReturn("encodedNewPass");
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            userService.resetPassword(resetDto);

            assertThat(user.getPassword()).isEqualTo("encodedNewPass");

            verify(userRepository).findByEmail("john@test.com");
            verify(bCryptPasswordEncoder).encode("newPassword");
            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when passwords do not match")
        void resetPassword_passwordMismatch() {
            UserPasswordResetDto resetDto = new UserPasswordResetDto()
                    .setEmail("john@test.com")
                    .setEnterPassword("password1")
                    .setConfirmPassword("password2");

            assertThatThrownBy(() -> userService.resetPassword(resetDto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Password do not match");

            verify(userRepository, never()).findByEmail(anyString());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when user email not found")
        void resetPassword_userNotFound() {
            UserPasswordResetDto resetDto = new UserPasswordResetDto()
                    .setEmail("unknown@test.com")
                    .setEnterPassword("newPassword")
                    .setConfirmPassword("newPassword");

            when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.resetPassword(resetDto))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("User not found");

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("should encode the new password before saving")
        void resetPassword_encodesPassword() {
            UserPasswordResetDto resetDto = new UserPasswordResetDto()
                    .setEmail("john@test.com")
                    .setEnterPassword("rawNew")
                    .setConfirmPassword("rawNew");

            when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(user));
            when(bCryptPasswordEncoder.encode("rawNew")).thenReturn("$2a$encodedNew");
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            userService.resetPassword(resetDto);

            assertThat(user.getPassword()).isEqualTo("$2a$encodedNew");
            verify(bCryptPasswordEncoder).encode("rawNew");
        }
    }
}
