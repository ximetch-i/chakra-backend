package com.chakra.service;

import com.chakra.dto.WeatherDataDTO;
import com.chakra.enums.District;

import java.time.LocalDate;

public interface WeatherProvider {

    WeatherDataDTO getWeather(District district, LocalDate plantingDate);
}
