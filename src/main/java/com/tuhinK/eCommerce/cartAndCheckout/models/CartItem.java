package com.tuhinK.eCommerce.cartAndCheckout.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tuhinK.eCommerce.commons.models.BaseModel;
import com.tuhinK.eCommerce.product.models.Product;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class CartItem extends BaseModel {

    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    public void setTotalPrice(){
        this.totalPrice = this.unitPrice.multiply(new BigDecimal(quantity));
    }

}
