package com.agrialert.service.impl;

import com.agrialert.client.NasaPowerClient;
import com.agrialert.dto.WeatherDataDTO;
import com.agrialert.enums.District;
import com.agrialert.enums.WeatherSource;
import com.agrialert.service.WeatherProvider;
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

    @Override
    public WeatherSource getSource() {
        return WeatherSource.HISTORICAL;
    }
}