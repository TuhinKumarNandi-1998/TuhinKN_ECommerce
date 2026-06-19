package com.tuhinK.eCommerce.auth.user;

import com.tuhinK.eCommerce.commons.exceptions.ResourceNotFoundException;
import com.tuhinK.eCommerce.user.models.Role;
import com.tuhinK.eCommerce.user.models.User;
import com.tuhinK.eCommerce.user.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EComUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EComUserDetailsService eComUserDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        Role role = new Role("ROLE_USER");
        user = new User()
                .setFirstName("John")
                .setEmail("john@test.com")
                .setPassword("password123")
                .setAllRoles(List.of(role));
        user.setId(1L);
    }

    @Nested
    @DisplayName("loadUserByUsername")
    class LoadUserByUsername {

        @Test
        @DisplayName("should return UserDetails when user exists")
        void loadUserByUsername_success() {
            when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(user));

            UserDetails userDetails = eComUserDetailsService.loadUserByUsername("john@test.com");

            assertThat(userDetails).isNotNull();
            assertThat(userDetails.getUsername()).isEqualTo("john@test.com");
            assertThat(userDetails.getPassword()).isEqualTo("password123");
            assertThat(userDetails.getAuthorities()).hasSize(1);
            assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_USER");
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when user not found")
        void loadUserByUsername_notFound() {
            when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> eComUserDetailsService.loadUserByUsername("unknown@test.com"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("User not found with email : unknown@test.com");
        }
    }
}
