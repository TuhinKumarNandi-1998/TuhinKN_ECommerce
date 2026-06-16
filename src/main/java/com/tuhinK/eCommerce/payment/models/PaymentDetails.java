package com.tuhinK.eCommerce.payment.models;

import com.tuhinK.eCommerce.commons.models.BaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@Entity
public class PaymentDetails extends BaseModel {

    private String orderId;
    private String paymentId;

    @Column(name = "payment_link", length = 1024)
    private String PaymentLink;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

}
