package com.antra.report.client.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BasicConfig {
    @Bean
    public ExecutorService threadPool() {
        return Executors.newFixedThreadPool(10);
    }

    @Bean
    public RestTemplate restTemplete() {
        return new RestTemplate();
    }
}