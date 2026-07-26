package com.chakra.service.impl;

import com.chakra.dto.WeatherDataDTO;
import com.chakra.enums.Crop;
import com.chakra.enums.RiskLevel;
import com.chakra.model.CropParameters;
import com.chakra.model.CropParameters.Params;
import com.chakra.model.CropParameters.Trapezoid;
import com.chakra.model.FuzzyMembership;
import com.chakra.service.AgriculturalModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Service
public class AgriculturalModelImpl implements AgriculturalModel {

    private static final double SCORE_FAVORABLE = 80.0;
    private static final double SCORE_CAUTION = 50.0;
    private static final int DAYS_IN_MONTH = 30;

    @Override
    public Result calculate(Crop crop, WeatherDataDTO weatherData) {
        Params params = CropParameters.of(crop);

        double scoreTemperature = normalizeTemperature(weatherData.getTemperature(), params.temperature());
        double scoreSoilMoisture = normalizeSoilMoisture(weatherData.getSoilMoisture(), params.soilMoisture());
        double scoreElevation = normalizeElevation(weatherData.getElevation(), params.elevation());
        double scorePrecipitation = normalizePrecipitation(
                weatherData.getPrecipitation(), params.annualPrecipMin(), params.annualPrecipMax());

        log.debug("Scores individuales - T:{} P:{} SM:{} A:{}",
                scoreTemperature, scorePrecipitation, scoreSoilMoisture, scoreElevation);

        double suitabilityIndex = scoreTemperature * params.weightTemperature()
                + scorePrecipitation * params.weightPrecipitation()
                + scoreSoilMoisture * params.weightSoilMoisture()
                + scoreElevation * params.weightElevation();

        double score = Math.round(suitabilityIndex * 100.0) / 100.0;
        RiskLevel riskLevel = classifyRisk(score);

        Map<String, Double> scores = new LinkedHashMap<>();
        scores.put("temperature", scoreTemperature);
        scores.put("precipitation", scorePrecipitation);
        scores.put("soilMoisture", scoreSoilMoisture);
        scores.put("elevation", scoreElevation);

        String recommendation = buildRecommendation(crop, riskLevel, scores, weatherData);

        log.info("Evaluación completada - score:{} risk:{} crop:{}", score, riskLevel, crop);

        return new Result(score, riskLevel, recommendation);
    }

    private double normalizeTemperature(double temperature, Trapezoid t) {
        return FuzzyMembership.trapezoidal(temperature, t.a(), t.b(), t.c(), t.d());
    }

    private double normalizeSoilMoisture(double soilMoisture, Trapezoid t) {
        double percentage = soilMoisture * 100.0;
        return FuzzyMembership.trapezoidal(percentage, t.a(), t.b(), t.c(), t.d());
    }

    private double normalizeElevation(double elevation, Trapezoid t) {
        return FuzzyMembership.trapezoidal(elevation, t.a(), t.b(), t.c(), t.d());
    }

    private double normalizePrecipitation(double dailyPrecipitation, double annualMin, double annualMax) {
        double monthlyPrecipitation = dailyPrecipitation * DAYS_IN_MONTH;
        double monthlyMin = annualMin / 365.0 * DAYS_IN_MONTH;
        double monthlyMax = annualMax / 365.0 * DAYS_IN_MONTH;

        if (monthlyPrecipitation < monthlyMin) {
            return Math.max(0, (monthlyPrecipitation / monthlyMin) * 100.0);
        }
        if (monthlyPrecipitation > monthlyMax) {
            return Math.max(0, ((2 * monthlyMax) - monthlyPrecipitation) / monthlyMax * 100.0);
        }
        return 100.0;
    }

    private RiskLevel classifyRisk(double score) {
        if (score >= SCORE_FAVORABLE) {
            return RiskLevel.FAVORABLE;
        } else if (score >= SCORE_CAUTION) {
            return RiskLevel.CAUTION;
        }
        return RiskLevel.NOT_RECOMMENDED;
    }

    private String buildRecommendation(Crop crop, RiskLevel riskLevel,
                                       Map<String, Double> scores, WeatherDataDTO weatherData) {
        String cropName = cropNameEs(crop);

        if (riskLevel == RiskLevel.FAVORABLE) {
            return String.format(
                    "Condiciones favorables para sembrar %s. Los parámetros climáticos se encuentran dentro de los rangos óptimos.",
                    cropName);
        }

        String limitingFactor = findLimitingFactor(scores);

        return switch (riskLevel) {
            case CAUTION -> String.format(
                    "Precaución: %s presenta condiciones moderadas para sembrar %s. %s " +
                            "Se recomienda monitorear las condiciones antes de la siembra.",
                    limitingFactor, cropName, limitingFactor);
            case NOT_RECOMMENDED -> String.format(
                    "No se recomienda sembrar %s en este momento. %s " +
                            "Considere cambiar la fecha de siembra o seleccionar una ubicación diferente.",
                    cropName, limitingFactor);
            default -> "";
        };
    }

    private String findLimitingFactor(Map<String, Double> scores) {
        String worst = scores.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("temperature");

        return switch (worst) {
            case "temperature" ->
                    "La temperatura está fuera del rango óptimo para este cultivo.";
            case "precipitation" ->
                    "La precipitación es insuficiente o excesiva para las necesidades del cultivo.";
            case "soilMoisture" ->
                    "La humedad del suelo no se encuentra en el rango adecuado.";
            case "elevation" ->
                    "La altitud no es adecuada para este cultivo en esta ubicación.";
            default ->
                    "Existen condiciones climáticas desfavorables.";
        };
    }

    private String cropNameEs(Crop crop) {
        return switch (crop) {
            case PAPA -> "papa";
            case MAIZ -> "maíz";
            case CAFE -> "café";
        };
    }
}
