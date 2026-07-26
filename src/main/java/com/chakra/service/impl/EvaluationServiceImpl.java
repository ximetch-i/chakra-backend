package com.chakra.service.impl;

import com.chakra.dto.WeatherDataDTO;
import com.chakra.dto.request.EvaluationRequest;
import com.chakra.dto.response.EvaluationResponse;
import com.chakra.entity.Evaluation;
import com.chakra.mapper.EvaluationMapper;
import com.chakra.repository.EvaluationRepository;
import com.chakra.service.AgriculturalModel;
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
    private final AgriculturalModel agriculturalModel;

    @Override
    public EvaluationResponse createEvaluation(EvaluationRequest request) {
        log.info("Consultando clima para distrito={}, cultivo={}, fecha={}",
                request.getDistrict(), request.getCrop(), request.getPlantingDate());

        WeatherDataDTO weatherData = nasaPowerClient.getHistoricalAverage(request.getDistrict(), request.getPlantingDate());

        AgriculturalModel.Result result = agriculturalModel.calculate(request.getCrop(), weatherData);

        Evaluation evaluation = evaluationMapper.toEntity(request);
        evaluation.setTemperature(weatherData.getTemperature());
        evaluation.setPrecipitation(weatherData.getPrecipitation());
        evaluation.setSoilMoisture(weatherData.getSoilMoisture());
        evaluation.setElevation(weatherData.getElevation());
        evaluation.setScore(result.score());
        evaluation.setRiskLevel(result.riskLevel());
        evaluation.setRecommendation(result.limitingFactor());

        Evaluation saved = evaluationRepository.save(evaluation);

        return evaluationMapper.toResponse(saved);
    }
}
