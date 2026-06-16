package com.tuhinK.eCommerce.cartandcheckout.services;

import com.tuhinK.eCommerce.cartandcheckout.dtos.AmountDto;
import com.tuhinK.eCommerce.cartandcheckout.dtos.CheckoutItemRequestDto;
import com.tuhinK.eCommerce.cartandcheckout.dtos.CheckoutSessionResponseDto;
import com.tuhinK.eCommerce.cartandcheckout.exception.CheckoutException;
import com.tuhinK.eCommerce.order.models.Order;
import com.tuhinK.eCommerce.order.models.OrderStatus;
import com.tuhinK.eCommerce.order.repositories.OrderRepository;
import com.tuhinK.eCommerce.payment.dtos.InitiatePaymentRequest;
import com.tuhinK.eCommerce.payment.dtos.PaymentLineItemDto;
import com.tuhinK.eCommerce.payment.dtos.PaymentSessionResponse;
import com.tuhinK.eCommerce.payment.exception.PaymentGatewayException;
import com.tuhinK.eCommerce.payment.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CheckoutServiceImp implements  CheckoutService {

    private final PaymentService paymentService;
    private final OrderRepository orderRepository;

    @Value("${baseURL}")
    private String baseURL;

    @Override
    public CheckoutSessionResponseDto createSession(List<CheckoutItemRequestDto> checkoutItemDtoList) throws CheckoutException {
        try {
            InitiatePaymentRequest paymentRequest = InitiatePaymentRequest.builder()
                    .idempotencyKey("payment_" + checkoutItemDtoList.getFirst().getOrderId())
                    .orderReferenceId(checkoutItemDtoList.getFirst().getOrderId())
                    .lineItems(createPaymentLineItems(checkoutItemDtoList))
                    .successUrl(baseURL + "payment/success")
                    .cancelUrl(baseURL + "payment/failed")
                    .build();

            PaymentSessionResponse paymentSessionResponse = paymentService.createGatewaySession(paymentRequest);

            // update order status
            updateOrderStatus(Long.parseLong(checkoutItemDtoList.getFirst().getOrderId()));

            return CheckoutSessionResponseDto.builder()
                    .orderId(checkoutItemDtoList.getFirst().getOrderId())
                    .checkoutSessionId(paymentSessionResponse.getGatewaySessionId())
                    .flowType(CheckoutSessionResponseDto.PaymentFlowType.REDIRECT)
                    .gatewayProvider(paymentSessionResponse.getGatewayProvider())
                    .checkoutUrl(paymentSessionResponse.getCheckoutUrl())
                    .cancelUrl(paymentSessionResponse.getCancelUrl())
                    .totalAmount(AmountDto.builder()
                            .value(paymentSessionResponse.getAmount())
                            .currency(paymentSessionResponse.getCurrency())
                            .build())
                    .build();

        } catch (PaymentGatewayException e) {
            throw new CheckoutException("checkout failed : " + e.getMessage());
        }
    }

    private List<PaymentLineItemDto> createPaymentLineItems(List<CheckoutItemRequestDto> checkoutItemDtoList) {
        return checkoutItemDtoList.stream()
                .map(checkoutLineItem -> {
                    if (checkoutItemDtoList.isEmpty() || checkoutLineItem.getQuantity() <= 0) throw new CheckoutException("Checkout item empty");
                    return new PaymentLineItemDto()
                            .setName(checkoutLineItem.getProductName())
                            .setDescription("item : " + checkoutLineItem.getProductName())
                            .setQuantity(checkoutLineItem.getQuantity())
                            .setUnitPrice(BigDecimal.valueOf(checkoutLineItem.getPrice()))
                            .setCurrency(checkoutLineItem.getCurrency());
                })
                .toList();
    }

    private void updateOrderStatus(long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CheckoutException("order not found with id " + orderId));
        order.setOrderStatus(OrderStatus.PROCESSING);
        orderRepository.save(order);
    }
}
