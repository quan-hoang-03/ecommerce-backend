package com.quanhm.ecommerce.be.service;

import com.paypal.orders.*;
import com.paypal.http.HttpResponse;
import com.quanhm.ecommerce.be.config.PayPalClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

@Service
public class PayPalService {

    private static final Logger logger = LoggerFactory.getLogger(PayPalService.class);

    @Autowired
    private PayPalClient payPalClient;

    // Tỷ giá VND -> USD (cập nhật theo thực tế)
    private static final double VND_TO_USD_RATE = 25000.0;

    public String createOrder(double amountVND, String returnUrl, String cancelUrl) throws IOException {
        // Chuyển đổi VND sang USD
        double amountUSD = amountVND / VND_TO_USD_RATE;
        // Đảm bảo tối thiểu 0.01 USD
        if (amountUSD < 0.01) {
            amountUSD = 0.01;
        }

        logger.info("Creating PayPal order: {} VND = {} USD", amountVND, amountUSD);
        logger.info("Return URL: {}, Cancel URL: {}", returnUrl, cancelUrl);

        try {
            OrderRequest orderRequest = new OrderRequest();
            orderRequest.checkoutPaymentIntent("CAPTURE");

            // cấu hình purchase unit (giống 1 hóa đơn nhỏ)
            // Sử dụng Locale.US để đảm bảo dấu chấm (.) làm decimal separator
            String formattedAmount = String.format(Locale.US, "%.2f", amountUSD);
            logger.info("Formatted amount for PayPal: {}", formattedAmount);
            
            PurchaseUnitRequest purchaseUnit = new PurchaseUnitRequest()
                    .amountWithBreakdown(new AmountWithBreakdown()
                            .currencyCode("USD")
                            .value(formattedAmount));

            ApplicationContext appContext = new ApplicationContext()
                    .returnUrl(returnUrl)
                    .cancelUrl(cancelUrl);

            orderRequest.applicationContext(appContext);
            orderRequest.purchaseUnits(List.of(purchaseUnit));

            OrdersCreateRequest request = new OrdersCreateRequest();
            request.requestBody(orderRequest);

            HttpResponse<Order> response = payPalClient.client().execute(request);
            logger.info("PayPal response status: {}", response.statusCode());
            
            for (LinkDescription link : response.result().links()) {
                logger.info("PayPal link: {} - {}", link.rel(), link.href());
                if ("approve".equals(link.rel())) {
                    return link.href();
                }
            }
            throw new IOException("No approval link found in PayPal response");
        } catch (Exception e) {
            logger.error("PayPal error: {}", e.getMessage(), e);
            throw new IOException("PayPal error: " + e.getMessage(), e);
        }
    }
}
