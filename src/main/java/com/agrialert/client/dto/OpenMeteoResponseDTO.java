package com.agrialert.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMeteoResponseDTO {

    private Daily daily;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Daily {
        private List<String> time;
        private List<Double> temperature_2m_mean;
        private List<Double> precipitation_sum;
        private List<Double> relative_humidity_2m_mean;
    }
}
