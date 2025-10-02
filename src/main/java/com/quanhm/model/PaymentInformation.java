package com.quanhm.model;

import jakarta.persistence.Column;

import java.time.LocalDate;

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
