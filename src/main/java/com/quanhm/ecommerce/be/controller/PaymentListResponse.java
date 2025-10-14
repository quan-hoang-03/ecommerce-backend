package com.quanhm.ecommerce.be.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RequestMapping("/api/payment")
public class PaymentListResponse {
    private String payment_link_url;
    private String payment_link_id;

    public PaymentListResponse(){

    }
    public PaymentListResponse(String payment_link_url, String payment_link_id) {
        this.payment_link_url = payment_link_url;
        this.payment_link_id = payment_link_id;
    }

    public String getPayment_link_url() {
        return payment_link_url;
    }

    public void setPayment_link_url(String payment_link_url) {
        this.payment_link_url = payment_link_url;
    }

    public String getPayment_link_id() {
        return payment_link_id;
    }

    public void setPayment_link_id(String payment_link_id) {
        this.payment_link_id = payment_link_id;
    }
}
