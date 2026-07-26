package com.chakra.dto.response;

import com.chakra.enums.RiskLevel;
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
    private Double temperature;
    private Double precipitation;
    private Double soilMoisture;
    private Double elevation;
    private Double score;
    private RiskLevel riskLevel;
    private String recommendation;
    private LocalDateTime createdAt;
}