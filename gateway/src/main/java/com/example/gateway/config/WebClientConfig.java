package com.example.gateway.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    // บยิง HTTP Request (เหมือน Postman แต่เป็นโค้ด) แบบ Non-blocking
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
