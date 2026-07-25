package com.agrialert.service;

import com.agrialert.dto.WeatherDataDTO;
import com.agrialert.enums.District;
import com.agrialert.enums.WeatherSource;

import java.time.LocalDate;

public interface WeatherProvider {

    WeatherDataDTO getWeather(District district, LocalDate plantingDate);
    WeatherSource getSource();
}