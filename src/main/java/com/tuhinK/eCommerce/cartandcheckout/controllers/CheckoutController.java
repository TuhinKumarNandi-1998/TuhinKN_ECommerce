package com.tuhinK.eCommerce.cartandcheckout.controllers;

import com.tuhinK.eCommerce.cartandcheckout.dtos.CheckoutItemRequestDto;
import com.tuhinK.eCommerce.cartandcheckout.dtos.CheckoutSessionResponseDto;
import com.tuhinK.eCommerce.cartandcheckout.exception.CheckoutException;
import com.tuhinK.eCommerce.cartandcheckout.services.CheckoutService;
import com.tuhinK.eCommerce.commons.dtos.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api_prefix}/checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;

    @PostMapping("/create-session")
    public ResponseEntity<ApiResponse> createCheckoutSession(@RequestBody List<CheckoutItemRequestDto> checkoutItemDtoList) {
        try {
            // Create a Stripe checkout session
            CheckoutSessionResponseDto checkoutSessionResponseDto = checkoutService.createSession(checkoutItemDtoList);
            return ResponseEntity.ok(new ApiResponse("Checkout session created successfully.!", checkoutSessionResponseDto));
        } catch (CheckoutException checkoutException) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("Checkout session error: " + checkoutException.getMessage(), null));
        } catch (Exception exception) {
            // Handle unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            new ApiResponse("An unexpected error occurred: " +  exception.getMessage(), null)
                    );
        }
    }
}
