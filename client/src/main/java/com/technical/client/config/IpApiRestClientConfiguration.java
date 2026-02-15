package com.technical.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class IpApiRestClientConfiguration {
    @Value("${ip.api.base.url}")
    private String baseUrl;

    @Bean
    public RestClient ipApiRestClient() {
        return RestClient.builder().baseUrl(baseUrl).build();
    }
}
