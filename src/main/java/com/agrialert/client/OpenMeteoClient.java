package com.agrialert.client;

import com.agrialert.client.dto.OpenMeteoResponseDTO;
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
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenMeteoClient {

    @Qualifier("openMeteoWebClient")
    private final WebClient openMeteoWebClient;

    private final DistrictCoordinatesResolver coordinatesResolver;

    public WeatherDataDTO getForecast(District district, LocalDate plantingDate) {
        DistrictCoordinatesResolver.Coordinates coordinates = coordinatesResolver.resolve(district);

        try {
            OpenMeteoResponseDTO response = openMeteoWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("latitude", coordinates.latitude())
                            .queryParam("longitude", coordinates.longitude())
                            .queryParam("daily", "temperature_2m_mean,precipitation_sum,relative_humidity_2m_mean")
                            .queryParam("timezone", "auto")
                            .queryParam("start_date", plantingDate)
                            .queryParam("end_date", plantingDate)
                            .build())
                    .retrieve()
                    .bodyToMono(OpenMeteoResponseDTO.class)
                    .block();

            return mapToWeatherData(response, plantingDate);

        } catch (Exception ex) {
            log.error("Error al consultar Open-Meteo para el distrito {}: {}", district, ex.getMessage());
            throw new ExternalApiException("No se pudo obtener el pronóstico de Open-Meteo", ex);
        }
    }

    private WeatherDataDTO mapToWeatherData(OpenMeteoResponseDTO response, LocalDate plantingDate) {
        if (response == null || response.getDaily() == null) {
            throw new ExternalApiException("Respuesta vacía de Open-Meteo");
        }

        List<String> dates = response.getDaily().getTime();
        int index = dates != null ? dates.indexOf(plantingDate.toString()) : -1;

        if (index == -1) {
            throw new ExternalApiException("Open-Meteo no devolvió datos para la fecha solicitada");
        }

        return WeatherDataDTO.builder()
                .temperature(response.getDaily().getTemperature_2m_mean().get(index))
                .precipitation(response.getDaily().getPrecipitation_sum().get(index))
                .humidity(response.getDaily().getRelative_humidity_2m_mean().get(index))
                .elevation(response.getElevation())
                .build();
    }
}