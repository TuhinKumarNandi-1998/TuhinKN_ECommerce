package com.tuhinK.eCommerce.cartandcheckout.services;

import com.tuhinK.eCommerce.cartandcheckout.dtos.CheckoutItemRequestDto;
import com.tuhinK.eCommerce.cartandcheckout.dtos.CheckoutSessionResponseDto;
import com.tuhinK.eCommerce.cartandcheckout.exception.CheckoutException;

import java.util.List;

public interface CheckoutService {

    CheckoutSessionResponseDto createSession(List<CheckoutItemRequestDto> checkoutItemDtoList) throws CheckoutException;

}
