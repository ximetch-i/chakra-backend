package com.chakra.service.impl;

import com.chakra.dto.WeatherDataDTO;
import com.chakra.dto.request.EvaluationRequest;
import com.chakra.dto.response.EvaluationResponse;
import com.chakra.entity.Evaluation;
import com.chakra.enums.RiskLevel;
import com.chakra.mapper.EvaluationMapper;
import com.chakra.repository.EvaluationRepository;
import com.chakra.service.EvaluationService;
import com.chakra.client.NasaPowerClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationServiceImpl implements EvaluationService {

    private final NasaPowerClient nasaPowerClient;
    private final EvaluationRepository evaluationRepository;
    private final EvaluationMapper evaluationMapper;

    @Override
    public EvaluationResponse createEvaluation(EvaluationRequest request) {
        log.info("Consultando clima para distrito={}, cultivo={}, fecha={}",
                request.getDistrict(), request.getCrop(), request.getPlantingDate());

        WeatherDataDTO weatherData = nasaPowerClient.getHistoricalAverage(request.getDistrict(), request.getPlantingDate());

        Evaluation evaluation = evaluationMapper.toEntity(request);
        evaluation.setTemperature(weatherData.getTemperature());
        evaluation.setPrecipitation(weatherData.getPrecipitation());
        evaluation.setHumidity(weatherData.getHumidity());
        evaluation.setSoilMoisture(weatherData.getSoilMoisture());
        evaluation.setElevation(weatherData.getElevation());

        // TODO: reemplazar por el motor matemático (AHP + lógica difusa)
        evaluation.setScore(80.0);
        evaluation.setRiskLevel(RiskLevel.FAVORABLE);
        evaluation.setRecommendation("Condiciones favorables para sembrar.");

        Evaluation saved = evaluationRepository.save(evaluation);

        return evaluationMapper.toResponse(saved);
    }
}
