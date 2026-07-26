package com.chakra.service.impl;

import com.chakra.client.NasaPowerClient;
import com.chakra.dto.WeatherDataDTO;
import com.chakra.enums.District;
import com.chakra.service.WeatherProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class HistoricalWeatherProvider implements WeatherProvider {

    private final NasaPowerClient nasaPowerClient;

    @Override
    public WeatherDataDTO getWeather(District district, LocalDate plantingDate) {
        return nasaPowerClient.getHistoricalAverage(district, plantingDate);
    }
}
