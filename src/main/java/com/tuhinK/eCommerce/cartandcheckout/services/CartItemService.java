package com.tuhinK.eCommerce.cartandcheckout.services;

import com.tuhinK.eCommerce.cartandcheckout.models.CartItem;
import com.tuhinK.eCommerce.product.exceptions.ProductNotFoundException;
import com.tuhinK.eCommerce.product.exceptions.ProductNotPresentException;

public interface CartItemService {

    void addCartItem(Long cartId, Long productId, int quantity) throws ProductNotFoundException;

    void removeCartItem(Long cartId, Long productId) throws ProductNotFoundException;

    void updateItemQuantity(Long cartId, Long productId, int quantity) throws ProductNotFoundException;

    CartItem getCartItem(Long cartId, Long productId);
}
