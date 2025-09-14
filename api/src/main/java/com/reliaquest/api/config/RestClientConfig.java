package com.reliaquest.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
    @Value("${employee.server.url}")
    String empServerURL;

    @Bean
    public RestClient restClient(RestClient.Builder builder) {
        return builder.baseUrl(empServerURL).build();
    }
}
