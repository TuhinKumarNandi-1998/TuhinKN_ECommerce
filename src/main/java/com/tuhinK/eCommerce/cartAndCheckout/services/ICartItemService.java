package com.tuhinK.eCommerce.cartAndCheckout.services;

import com.tuhinK.eCommerce.cartAndCheckout.models.CartItem;
import com.tuhinK.eCommerce.product.exceptions.ProductNotPresentException;

public interface ICartItemService {

    void addCartItem(Long cartId, Long productId, int quantity) throws ProductNotPresentException;

    void removeCartItem(Long cartId, Long productId) throws ProductNotPresentException;

    void updateItemQuantity(Long cartId, Long productId, int quantity) throws ProductNotPresentException;

    CartItem getCartItem(Long cartId, Long productId);
}
