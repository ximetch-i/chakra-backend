package com.agrialert.service.impl;

import com.agrialert.dto.WeatherDataDTO;
import com.agrialert.dto.request.EvaluationRequest;
import com.agrialert.dto.response.EvaluationResponse;
import com.agrialert.entity.Evaluation;
import com.agrialert.enums.RiskLevel;
import com.agrialert.enums.WeatherSource;
import com.agrialert.mapper.EvaluationMapper;
import com.agrialert.repository.EvaluationRepository;
import com.agrialert.service.EvaluationService;
import com.agrialert.service.WeatherProvider;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationServiceImpl implements EvaluationService {

    private static final int FORECAST_THRESHOLD_DAYS = 14;

    private final List<WeatherProvider> weatherProviders;
    private final EvaluationRepository evaluationRepository;
    private final EvaluationMapper evaluationMapper;

    private Map<WeatherSource, WeatherProvider> providersBySource;

    @PostConstruct
    void init() {
        this.providersBySource = weatherProviders.stream()
                .collect(Collectors.toMap(WeatherProvider::getSource, Function.identity()));
    }

    @Override
    public EvaluationResponse createEvaluation(EvaluationRequest request) {
        WeatherSource source = resolveSource(request.getPlantingDate());
        WeatherProvider provider = providersBySource.get(source);

        log.info("Consultando clima para distrito={}, cultivo={}, fecha={}, fuente={}",
                request.getDistrict(), request.getCrop(), request.getPlantingDate(), source);

        WeatherDataDTO weatherData = provider.getWeather(request.getDistrict(), request.getPlantingDate());

        Evaluation evaluation = evaluationMapper.toEntity(request);
        evaluation.setWeatherSource(source);
        evaluation.setTemperature(weatherData.getTemperature());
        evaluation.setPrecipitation(weatherData.getPrecipitation());
        evaluation.setHumidity(weatherData.getHumidity());

        // TODO: reemplazar por el motor matemático (AHP + lógica difusa)
        evaluation.setScore(80.0);
        evaluation.setRiskLevel(RiskLevel.FAVORABLE);
        evaluation.setRecommendation("Condiciones favorables para sembrar.");

        Evaluation saved = evaluationRepository.save(evaluation);

        return evaluationMapper.toResponse(saved);
    }

    private WeatherSource resolveSource(LocalDate plantingDate) {
        LocalDate forecastLimit = LocalDate.now().plusDays(FORECAST_THRESHOLD_DAYS);
        return plantingDate.isAfter(forecastLimit) ? WeatherSource.HISTORICAL : WeatherSource.FORECAST;
    }
}