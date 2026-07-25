package com.agrialert.dto.response;

import com.agrialert.enums.RiskLevel;
import com.agrialert.enums.WeatherSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationResponse {

    private Long id;
    private String district;
    private String crop;
    private LocalDate plantingDate;
    private WeatherSource weatherSource;
    private Double temperature;
    private Double precipitation;
    private Double humidity;
    private Double score;
    private RiskLevel riskLevel;
    private String recommendation;
    private LocalDateTime createdAt;
}