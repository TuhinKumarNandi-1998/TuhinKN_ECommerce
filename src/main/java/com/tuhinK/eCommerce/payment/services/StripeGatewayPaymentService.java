package com.tuhinK.eCommerce.payment.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import com.stripe.param.checkout.SessionCreateParams;
import com.tuhinK.eCommerce.payment.dtos.InitiatePaymentRequest;
import com.tuhinK.eCommerce.payment.dtos.PaymentLineItemDto;
import com.tuhinK.eCommerce.payment.dtos.PaymentSessionResponse;
import com.tuhinK.eCommerce.payment.exception.PaymentGatewayException;
import com.tuhinK.eCommerce.payment.models.PaymentDetails;
import com.tuhinK.eCommerce.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class StripeGatewayPaymentService implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Value("${STRIPE_SECRET_KEY}")
    private String secretKey;

    @Override
    public PaymentSessionResponse createGatewaySession(InitiatePaymentRequest paymentRequest) throws PaymentGatewayException {
        try {

            Stripe.apiKey = secretKey;

            // Convert checkout items to session line items
            List<SessionCreateParams.LineItem> sessionItemsList = paymentRequest.getLineItems().stream()
                    .map(this::createSessionLineItem)
                    .toList();

            // Build the session parameters
            SessionCreateParams params = SessionCreateParams.builder()
                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setCancelUrl(paymentRequest.getCancelUrl())
                    .addAllLineItem(sessionItemsList)
                    .setSuccessUrl(paymentRequest.getSuccessUrl())
                    .build();

            // Pass your business idempotency key safely to Stripe's network engine
            RequestOptions options = RequestOptions.builder()
                    .setIdempotencyKey(paymentRequest.getIdempotencyKey())
                    .build();

            // Create the session
            Session session = Session.create(params, options);

            // create payment details that is to persist
            PaymentDetails paymentDetails = new PaymentDetails()
                    .setPaymentLink(session.getUrl())
                    .setOrderId(paymentRequest.getOrderReferenceId());

            paymentRepository.save(paymentDetails);

            return PaymentSessionResponse.builder()
                    .paymentTransactionId("TXN_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                    .gatewayProvider("STRIPE")
                    .gatewaySessionId(session.getId())
                    .checkoutUrl(session.getUrl())
                    .cancelUrl(session.getCancelUrl())
                    .clientSecret(session.getClientSecret())
                    .publicKey(null)
                    .amount(calculateTotalAmount(paymentRequest))
                    .currency(paymentRequest.getLineItems().getFirst().getCurrency())
                    .build();

        } catch (StripeException e) {
            // In enterprise applications, log cleanly and map to a specialized internal exception
            throw new PaymentGatewayException("payment gateway engine failed to spin up financial gateway context", e);
        }
    }

    private BigDecimal calculateTotalAmount(InitiatePaymentRequest paymentRequest) {
        return paymentRequest.getLineItems().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private SessionCreateParams.LineItem createSessionLineItem(PaymentLineItemDto paymentLineItemDto) {
        return SessionCreateParams.LineItem.builder()
                .setPriceData(createPriceData(paymentLineItemDto))
                .setQuantity((long) paymentLineItemDto.getQuantity())
                .build();
    }

    private SessionCreateParams.LineItem.PriceData createPriceData(PaymentLineItemDto paymentLineItemDto) {
        return SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency(paymentLineItemDto.getCurrency() != null ? paymentLineItemDto.getCurrency() : "inr")
                .setUnitAmount(convertToMinorUnits(paymentLineItemDto.getUnitPrice(), paymentLineItemDto.getCurrency()))
                .setProductData(
                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                .setName(paymentLineItemDto.getName())
                                .build()
                )
                .build();
    }

    /**
     * Translates standard decimal inputs safely into minor fractional currency integers.
     */
    private long convertToMinorUnits(BigDecimal amount, String currency) {
        // Zero-decimal currencies check list adjustment can be appended here if multi-currency global scale is needed
        return amount.multiply(BigDecimal.valueOf(100)).longValue();
    }
}
