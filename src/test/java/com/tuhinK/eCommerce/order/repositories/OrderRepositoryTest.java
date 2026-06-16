package com.tuhinK.eCommerce.order.repositories;

import com.tuhinK.eCommerce.order.models.Order;
import com.tuhinK.eCommerce.order.models.OrderStatus;
import com.tuhinK.eCommerce.user.models.User;
import com.tuhinK.eCommerce.user.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    private User savedUser;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("password");
        user.setFirstName("John");
        user.setLastName("Doe");
        savedUser = userRepository.save(user);

        Order order1 = new Order();
        order1.setUser(savedUser);
        order1.setOrderStatus(OrderStatus.PENDING);
        order1.setOrderDate(LocalDate.now());
        order1.setTotalAmount(new BigDecimal("100.00"));
        orderRepository.save(order1);

        Order order2 = new Order();
        order2.setUser(savedUser);
        order2.setOrderStatus(OrderStatus.DELIVERED);
        order2.setOrderDate(LocalDate.now());
        order2.setTotalAmount(new BigDecimal("200.00"));
        orderRepository.save(order2);
    }

    @Test
    @DisplayName("findByUserId should return all orders for a specific user")
    void findByUserId_found() {
        List<Order> result = orderRepository.findByUserId(savedUser.getId());

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(o -> o.getUser().getId().equals(savedUser.getId()));
    }

    @Test
    @DisplayName("findByUserId should return empty list if user has no orders")
    void findByUserId_notFound() {
        List<Order> result = orderRepository.findByUserId(99L);

        assertThat(result).isEmpty();
    }
}
