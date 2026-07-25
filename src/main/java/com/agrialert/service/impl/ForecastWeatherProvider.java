package com.agrialert.service.impl;

import com.agrialert.client.OpenMeteoClient;
import com.agrialert.dto.WeatherDataDTO;
import com.agrialert.enums.District;
import com.agrialert.enums.WeatherSource;
import com.agrialert.service.WeatherProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class ForecastWeatherProvider implements WeatherProvider {

    private final OpenMeteoClient openMeteoClient;

    @Override
    public WeatherDataDTO getWeather(District district, LocalDate plantingDate) {
        return openMeteoClient.getForecast(district, plantingDate);
    }

    @Override
    public WeatherSource getSource() {
        return WeatherSource.FORECAST;
    }
}