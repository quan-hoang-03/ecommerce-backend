package com.quanhm.ecommerce.be.service;

import com.paypal.orders.*;
import com.paypal.http.HttpResponse;
import com.quanhm.ecommerce.be.config.PayPalClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class PayPalService {

    @Autowired
    private PayPalClient payPalClient;

    public String createOrder(double amount, String returnUrl, String cancelUrl) throws IOException {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");

        // cấu hình purchase unit (giống 1 hóa đơn nhỏ)
        PurchaseUnitRequest purchaseUnit = new PurchaseUnitRequest()
                .amountWithBreakdown(new AmountWithBreakdown()
                        .currencyCode("USD")
                        .value(String.format("%.2f", amount)));

        ApplicationContext appContext = new ApplicationContext()
                .returnUrl(returnUrl)
                .cancelUrl(cancelUrl);

        orderRequest.applicationContext(appContext);
        orderRequest.purchaseUnits(List.of(purchaseUnit));

        OrdersCreateRequest request = new OrdersCreateRequest();
        request.requestBody(orderRequest);

        HttpResponse<Order> response = payPalClient.client().execute(request);
        for (LinkDescription link : response.result().links()) {
            if ("approve".equals(link.rel())) {
                return link.href(); // link để frontend redirect
            }
        }
        throw new IOException("No approval link found in PayPal response");
    }
}
