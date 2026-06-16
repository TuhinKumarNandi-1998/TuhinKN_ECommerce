package com.tuhinK.eCommerce.cartandcheckout.repositories;

import com.tuhinK.eCommerce.cartandcheckout.models.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    void deleteAllByCartId(Long cartId);
}
