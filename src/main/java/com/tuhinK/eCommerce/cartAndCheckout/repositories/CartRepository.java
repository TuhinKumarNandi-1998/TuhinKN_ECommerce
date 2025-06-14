package com.tuhinK.eCommerce.cartAndCheckout.repositories;

import com.tuhinK.eCommerce.cartAndCheckout.models.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Cart findByUserId(Long userId);
}
