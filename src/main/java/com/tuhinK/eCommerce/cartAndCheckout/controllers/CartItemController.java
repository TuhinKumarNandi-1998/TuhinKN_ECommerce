package com.tuhinK.eCommerce.cartAndCheckout.controllers;


import com.tuhinK.eCommerce.cartAndCheckout.models.Cart;
import com.tuhinK.eCommerce.cartAndCheckout.services.ICartItemService;
import com.tuhinK.eCommerce.cartAndCheckout.services.ICartService;
import com.tuhinK.eCommerce.commons.dtos.ApiResponse;
import com.tuhinK.eCommerce.commons.exceptions.ResourceNotFoundException;
import com.tuhinK.eCommerce.product.exceptions.ProductNotPresentException;
import com.tuhinK.eCommerce.product.services.IProductService;
import com.tuhinK.eCommerce.user.models.User;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api_prefix}/cartItems")
public class CartItemController {

    private final ICartItemService cartItemService;
    private final ICartService cartService;
    private final IProductService productService;
//    private final IUserService userService;

    @PostMapping("/item/add")
    public ResponseEntity<ApiResponse> addItemToCart(
                                                     @RequestParam Long productId,
                                                     @RequestParam Integer quantity) {
        try {
            // User user = userService.getAuthenticatedUser();
            Cart cart = cartService.initializeNewCart(new User());
            cartItemService.addCartItem(cart.getId(), productId, quantity);
            return ResponseEntity.ok().body(new ApiResponse("Item added to cart", null));
        } catch (ProductNotPresentException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch(JwtException e) {
            return ResponseEntity.status(UNAUTHORIZED).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @DeleteMapping("/cart/{cartId}/item/{itemId}/remove")
    public ResponseEntity<ApiResponse> removeItem(@PathVariable Long cartId,
                                                  @PathVariable Long itemId) {
        try {
            cartItemService.removeCartItem(cartId, itemId);
            return ResponseEntity.ok().body(new ApiResponse("Item removed from cart", null));
        } catch (ResourceNotFoundException | ProductNotPresentException e) {
            return ResponseEntity.status(404).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping("/cart/{cartId}/item/{itemId}/update")
    public ResponseEntity<ApiResponse> updateItemQuantity(@PathVariable Long cartId,
                                                          @PathVariable Long itemId,
                                                          @RequestParam Integer quantity) {
        try {
            cartItemService.updateItemQuantity(cartId, itemId, quantity);
            return ResponseEntity.ok().body(new ApiResponse("Item updated", null));
        } catch (ResourceNotFoundException | ProductNotPresentException e) {
            return ResponseEntity.status(404).body(new ApiResponse(e.getMessage(), null));
        }
    }
}
