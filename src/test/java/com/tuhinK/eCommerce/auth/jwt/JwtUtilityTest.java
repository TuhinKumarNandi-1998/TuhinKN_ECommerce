package com.tuhinK.eCommerce.auth.jwt;

import com.tuhinK.eCommerce.auth.user.EComUserDetails;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtUtilityTest {

    private JwtUtility jwtUtility;

    @Mock
    private Authentication authentication;

    private EComUserDetails userDetails;

    private final String testSecret = "36763979244226452948404D635166546A576D5A7134743777217A25432A462D"; // 256-bit secret

    @BeforeEach
    void setUp() {
        jwtUtility = new JwtUtility();
        ReflectionTestUtils.setField(jwtUtility, "jwtSecret", testSecret);
        ReflectionTestUtils.setField(jwtUtility, "jwtExpirationTime", 3600000); // 1 hour

        userDetails = new EComUserDetails()
                .setId(1L)
                .setEmail("test@test.com")
                .setAuthorities(List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Nested
    @DisplayName("generateTokenForUser")
    class GenerateTokenForUser {

        @Test
        @DisplayName("should generate valid JWT token")
        void generateTokenForUser_success() {
            when(authentication.getPrincipal()).thenReturn(userDetails);

            String token = jwtUtility.generateTokenForUser(authentication);

            assertThat(token).isNotBlank();
            
            // Validate the generated token
            assertThat(jwtUtility.validateJwtToken(token)).isTrue();
            assertThat(jwtUtility.getUsernameFromJwtToken(token)).isEqualTo("test@test.com");
        }
    }

    @Nested
    @DisplayName("validateJwtToken")
    class ValidateJwtToken {

        @Test
        @DisplayName("should throw JwtException for invalid token")
        void validateJwtToken_invalid() {
            String invalidToken = "invalid.token.here";

            assertThatThrownBy(() -> jwtUtility.validateJwtToken(invalidToken))
                    .isInstanceOf(JwtException.class);
        }
    }

    @Nested
    @DisplayName("extractUserDetailsFromToken")
    class ExtractUserDetailsFromToken {

        @Test
        @DisplayName("should extract correct EComUserDetails from token")
        void extractUserDetailsFromToken_success() {
            when(authentication.getPrincipal()).thenReturn(userDetails);
            String token = jwtUtility.generateTokenForUser(authentication);

            EComUserDetails extractedUser = jwtUtility.extractUserDetailsFromToken(token);

            assertThat(extractedUser).isNotNull();
            assertThat(extractedUser.getId()).isEqualTo(1L);
            assertThat(extractedUser.getUsername()).isEqualTo("test@test.com");
            assertThat(extractedUser.getAuthorities()).hasSize(1);
            assertThat(extractedUser.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_USER");
        }
    }
}
