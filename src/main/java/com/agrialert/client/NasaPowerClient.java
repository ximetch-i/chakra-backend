package com.agrialert.client;

import com.agrialert.client.dto.NasaPowerClimatologyResponseDTO;
import com.agrialert.dto.WeatherDataDTO;
import com.agrialert.enums.District;
import com.agrialert.exception.ExternalApiException;
import com.agrialert.util.DistrictCoordinatesResolver;
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
                            .queryParam("parameters", "T2M,PRECTOTCORR,RH2M")
                            .queryParam("community", "AG")
                            .queryParam("longitude", coordinates.longitude())
                            .queryParam("latitude", coordinates.latitude())
                            .queryParam("format", "JSON")
                            .build())
                    .retrieve()
                    .bodyToMono(NasaPowerClimatologyResponseDTO.class)
                    .block();

            return mapToWeatherData(response, monthKey);

        } catch (Exception ex) {
            log.error("Error al consultar NASA POWER para el distrito {}: {}", district, ex.getMessage());
            throw new ExternalApiException("No se pudo obtener el histórico de NASA POWER", ex);
        }
    }

    private WeatherDataDTO mapToWeatherData(NasaPowerClimatologyResponseDTO response, String monthKey) {
        if (response == null || response.getProperties() == null || response.getProperties().getParameter() == null) {
            throw new ExternalApiException("Respuesta vacía de NASA POWER");
        }

        Map<String, Map<String, Double>> parameter = response.getProperties().getParameter();

        Double temperature = extractValue(parameter, "T2M", monthKey);
        Double precipitation = extractValue(parameter, "PRECTOTCORR", monthKey);
        Double humidity = extractValue(parameter, "RH2M", monthKey);

        Double elevation = null;
        if (response.getGeometry() != null && response.getGeometry().getCoordinates() != null
                && response.getGeometry().getCoordinates().size() > 2) {
            elevation = response.getGeometry().getCoordinates().get(2);
        }

        return WeatherDataDTO.builder()
                .temperature(temperature)
                .precipitation(precipitation)
                .humidity(humidity)
                .elevation(elevation)
                .build();
    }

    private Double extractValue(Map<String, Map<String, Double>> parameter, String param, String monthKey) {
        Map<String, Double> values = parameter.get(param);
        if (values == null || !values.containsKey(monthKey)) {
            throw new ExternalApiException("NASA POWER no devolvió el parámetro " + param + " para " + monthKey);
        }
        return values.get(monthKey);
    }
}