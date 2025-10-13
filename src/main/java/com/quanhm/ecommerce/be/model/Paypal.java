package com.quanhm.ecommerce.be.model;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Paypal {
    @Value("${paypal.client-id}")
    private String clientId;

    @Value("${paypal.secret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String mode;

    @Bean
    public PayPalHttpClient payPalHttpClient() {
        PayPalEnvironment environment = "sandbox".equalsIgnoreCase(mode)
                ? new PayPalEnvironment.Sandbox(clientId, clientSecret)
                : new PayPalEnvironment.Live(clientId, clientSecret);

        return new PayPalHttpClient(environment);
    }
}
