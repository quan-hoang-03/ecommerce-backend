package com.quanhm.ecommerce.be.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Embeddable
public class PaymentInformation {
    @Column(name="card_name")
    private String cardName;
    @Column(name="card_number")
    private  String cardNumber;
    @Column(name="expiration_date")
    private LocalDate expirationDate;
    @Column(name="cvv")
    private String cvv;
}
