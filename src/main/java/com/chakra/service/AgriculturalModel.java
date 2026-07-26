package com.chakra.service;

import com.chakra.dto.WeatherDataDTO;
import com.chakra.enums.Crop;
import com.chakra.enums.RiskLevel;

public interface AgriculturalModel {

    record Result(
            double score,
            RiskLevel riskLevel,
            String limitingFactor
    ) {}

    Result calculate(Crop crop, WeatherDataDTO weatherData);
}
