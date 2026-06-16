package com.tuhinK.eCommerce.user.repositories;

import com.tuhinK.eCommerce.user.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User savedUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        User user1 = new User()
                .setFirstName("John")
                .setMiddleName("M")
                .setLastName("Doe")
                .setEmail("john@test.com")
                .setPassword("encodedPassword1");
        savedUser = userRepository.save(user1);

        User user2 = new User()
                .setFirstName("Jane")
                .setMiddleName("A")
                .setLastName("Smith")
                .setEmail("jane@test.com")
                .setPassword("encodedPassword2");
        userRepository.save(user2);
    }

    @Test
    @DisplayName("existsByEmail should return true when email exists")
    void existsByEmail_true() {
        Boolean exists = userRepository.existsByEmail("john@test.com");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByEmail should return false when email does not exist")
    void existsByEmail_false() {
        Boolean exists = userRepository.existsByEmail("nonexistent@test.com");

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("findByEmail should return user when email exists")
    void findByEmail_found() {
        Optional<User> result = userRepository.findByEmail("john@test.com");

        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("John");
        assertThat(result.get().getLastName()).isEqualTo("Doe");
        assertThat(result.get().getEmail()).isEqualTo("john@test.com");
    }

    @Test
    @DisplayName("findByEmail should return empty when email does not exist")
    void findByEmail_notFound() {
        Optional<User> result = userRepository.findByEmail("nonexistent@test.com");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByEmail should return correct user among multiple users")
    void findByEmail_correctUser() {
        Optional<User> result = userRepository.findByEmail("jane@test.com");

        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("Jane");
        assertThat(result.get().getLastName()).isEqualTo("Smith");
    }

    @Test
    @DisplayName("findById should return user when id exists")
    void findById_found() {
        Optional<User> result = userRepository.findById(savedUser.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("john@test.com");
    }

    @Test
    @DisplayName("findById should return empty when id does not exist")
    void findById_notFound() {
        Optional<User> result = userRepository.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("save should persist a new user and generate id")
    void save_newUser() {
        User newUser = new User()
                .setFirstName("Bob")
                .setLastName("Brown")
                .setEmail("bob@test.com")
                .setPassword("encodedPass");

        User saved = userRepository.save(newUser);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("bob@test.com");

        Optional<User> found = userRepository.findByEmail("bob@test.com");
        assertThat(found).isPresent();
    }

    @Test
    @DisplayName("delete should remove user from database")
    void delete_user() {
        userRepository.delete(savedUser);

        Optional<User> result = userRepository.findById(savedUser.getId());
        assertThat(result).isEmpty();

        Boolean exists = userRepository.existsByEmail("john@test.com");
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("save should update existing user")
    void save_updateExisting() {
        savedUser.setFirstName("UpdatedJohn");
        userRepository.save(savedUser);

        Optional<User> result = userRepository.findById(savedUser.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("UpdatedJohn");
        assertThat(result.get().getEmail()).isEqualTo("john@test.com");
    }
}
