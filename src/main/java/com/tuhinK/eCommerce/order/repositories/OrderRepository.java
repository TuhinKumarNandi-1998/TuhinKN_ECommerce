package com.tuhinK.eCommerce.order.repositories;

import com.tuhinK.eCommerce.order.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
}
