package com.agrialert.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class NasaPowerClimatologyResponseDTO {

    private Geometry geometry;
    private Properties properties;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Geometry {
        private List<Double> coordinates;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Properties {
        private Map<String, Map<String, Double>> parameter;
    }
}
