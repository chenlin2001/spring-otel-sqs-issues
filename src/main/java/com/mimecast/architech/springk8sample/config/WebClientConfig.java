package com.mimecast.architech.springk8sample.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient webClient(WebClient.Builder builder, @Value("${springTemplateApplicationBaseUrl}") String templateAppBaseUrl) {
        return builder.baseUrl(templateAppBaseUrl).build();
    }
}
