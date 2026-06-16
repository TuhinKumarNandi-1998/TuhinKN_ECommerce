package com.tuhinK.eCommerce.cartandcheckout.services;

import com.tuhinK.eCommerce.cartandcheckout.models.Cart;
import com.tuhinK.eCommerce.user.models.User;

import java.math.BigDecimal;

public interface CartService {

    Cart initializeNewCart(User user);

    Cart getCartByUserId(Long userId);

    Cart getCart(Long cartId);

    void clearCart(Long cartId);

    BigDecimal getTotalPrice(Long cartId);
}
