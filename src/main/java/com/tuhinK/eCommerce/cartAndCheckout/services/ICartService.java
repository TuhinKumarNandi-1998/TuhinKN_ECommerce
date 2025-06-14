package com.tuhinK.eCommerce.cartAndCheckout.services;

import com.tuhinK.eCommerce.cartAndCheckout.models.Cart;
import com.tuhinK.eCommerce.user.models.User;

import java.math.BigDecimal;

public interface ICartService {

    Cart initializeNewCart(User user);

    Cart getCartByUserId(Long userId);

    Cart getCart(Long cartId);

    void clearCart(Long cartId);

    BigDecimal getTotalPrice(Long cartId);
}
