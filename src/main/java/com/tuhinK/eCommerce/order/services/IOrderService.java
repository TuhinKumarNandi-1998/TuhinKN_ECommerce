package com.tuhinK.eCommerce.order.services;

import com.tuhinK.eCommerce.order.dtos.OrderDto;
import com.tuhinK.eCommerce.order.models.Order;

import java.util.List;

public interface IOrderService {

    Order placeOrder(Long userId);

    OrderDto getOrder(Long orderId);

    List<OrderDto> getOrdersByUserId(Long userId);

    OrderDto convertToDto(Order order);
}
