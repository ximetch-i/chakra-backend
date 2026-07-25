package com.agrialert.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private final WeatherApiProperties weatherApiProperties;

    @Bean
    public WebClient openMeteoWebClient() {
        return WebClient.builder()
                .baseUrl(weatherApiProperties.getOpenMeteo().getBaseUrl())
                .build();
    }

    @Bean
    public WebClient nasaPowerWebClient() {
        return WebClient.builder()
                .baseUrl(weatherApiProperties.getNasaPower().getBaseUrl())
                .build();
    }
}
