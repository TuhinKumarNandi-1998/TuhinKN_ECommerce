package com.tuhinK.eCommerce.cartandcheckout.repositories;

import com.tuhinK.eCommerce.cartandcheckout.models.Cart;
import com.tuhinK.eCommerce.user.models.User;
import com.tuhinK.eCommerce.user.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    private User savedUser;
    private Cart savedCart;

    @BeforeEach
    void setUp() {
        cartRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User()
                .setFirstName("Jane")
                .setLastName("Doe")
                .setEmail("jane@test.com")
                .setPassword("pass");
        savedUser = userRepository.save(user);

        Cart cart = new Cart();
        cart.setUser(savedUser);
        savedCart = cartRepository.save(cart);
    }

    @Test
    @DisplayName("should find cart by user ID")
    void findByUserId_success() {
        Cart result = cartRepository.findByUserId(savedUser.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(savedCart.getId());
        assertThat(result.getUser().getId()).isEqualTo(savedUser.getId());
    }

    @Test
    @DisplayName("should return null if cart not found for user ID")
    void findByUserId_notFound() {
        Cart result = cartRepository.findByUserId(999L);
        assertThat(result).isNull();
    }
}
