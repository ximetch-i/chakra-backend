package com.agrialert.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "chakra.weather")
public class WeatherApiProperties {

    private OpenMeteo openMeteo = new OpenMeteo();
    private NasaPower nasaPower = new NasaPower();

    @Getter @Setter
    public static class OpenMeteo {
        private String baseUrl;
        private int forecastDays;
    }

    @Getter @Setter
    public static class NasaPower {
        private String baseUrl;
        private String community;
        private String parameters;
    }
}
