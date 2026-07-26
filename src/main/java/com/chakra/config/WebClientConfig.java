package com.chakra.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private final WeatherApiProperties weatherApiProperties;

    @Bean
    public WebClient nasaPowerWebClient() {
        return WebClient.builder()
                .baseUrl(weatherApiProperties.getNasaPower().getBaseUrl())
                .build();
    }
}
