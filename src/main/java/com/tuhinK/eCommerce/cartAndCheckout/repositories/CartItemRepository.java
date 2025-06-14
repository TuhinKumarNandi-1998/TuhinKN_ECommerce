package com.tuhinK.eCommerce.cartAndCheckout.repositories;

import com.tuhinK.eCommerce.cartAndCheckout.models.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    void deleteAllByCartId(Long cartId);
}
