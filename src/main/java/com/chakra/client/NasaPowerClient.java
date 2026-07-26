package com.chakra.client;

import com.chakra.client.dto.NasaPowerClimatologyResponseDTO;
import com.chakra.dto.WeatherDataDTO;
import com.chakra.enums.District;
import com.chakra.exception.ExternalApiException;
import com.chakra.util.DistrictCoordinatesResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NasaPowerClient {

    @Qualifier("nasaPowerWebClient")
    private final WebClient nasaPowerWebClient;

    private final DistrictCoordinatesResolver coordinatesResolver;

    public WeatherDataDTO getHistoricalAverage(District district, LocalDate plantingDate) {
        DistrictCoordinatesResolver.Coordinates coordinates = coordinatesResolver.resolve(district);
        String monthKey = plantingDate.getMonth()
                .getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                .toUpperCase(); // Ej: "AUG"

        try {
            NasaPowerClimatologyResponseDTO response = nasaPowerWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("parameters", "T2M,PRECTOTCORR,GWETTOP")
                            .queryParam("community", "AG")
                            .queryParam("longitude", coordinates.longitude())
                            .queryParam("latitude", coordinates.latitude())
                            .queryParam("format", "JSON")
                            .build())
                    .retrieve()
                    .bodyToMono(NasaPowerClimatologyResponseDTO.class)
                    .block();

            return mapToWeatherData(response, monthKey, coordinates);

        } catch (Exception ex) {
            log.error("Error al consultar NASA POWER para el distrito {}: {}", district, ex.getMessage());
            throw new ExternalApiException("Could not retrieve NASA POWER historical data", ex);
        }
    }

    private WeatherDataDTO mapToWeatherData(NasaPowerClimatologyResponseDTO response, String monthKey,
                                           DistrictCoordinatesResolver.Coordinates coordinates) {
        if (response == null || response.getProperties() == null || response.getProperties().getParameter() == null) {
            throw new ExternalApiException("Empty response from NASA POWER");
        }

        Map<String, Map<String, Double>> parameter = response.getProperties().getParameter();

        Double temperature = extractValue(parameter, "T2M", monthKey);
        Double precipitation = extractValue(parameter, "PRECTOTCORR", monthKey);
        Double soilMoisture = extractValue(parameter, "GWETTOP", monthKey);

        double gridElevation = response.getGeometry().getCoordinates().get(2);
        double correctedTemperature = temperature + (gridElevation - coordinates.elevation()) * 0.0065;

        log.debug("Temp NASA={} C, gridElev={}m, realElev={}m, corrected={} C",
                temperature, gridElevation, coordinates.elevation(), correctedTemperature);

        return WeatherDataDTO.builder()
                .temperature(correctedTemperature)
                .precipitation(precipitation)
                .soilMoisture(soilMoisture)
                .elevation(coordinates.elevation())
                .build();
    }

    private Double extractValue(Map<String, Map<String, Double>> parameter, String param, String monthKey) {
        Map<String, Double> values = parameter.get(param);
        if (values == null || !values.containsKey(monthKey)) {
            throw new ExternalApiException("NASA POWER did not return parameter " + param + " for " + monthKey);
        }
        return values.get(monthKey);
    }
}