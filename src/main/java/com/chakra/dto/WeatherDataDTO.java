package com.chakra.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherDataDTO {

    private Double temperature;
    private Double precipitation;
    private Double humidity;
    private Double soilMoisture;
    private Double elevation;
}