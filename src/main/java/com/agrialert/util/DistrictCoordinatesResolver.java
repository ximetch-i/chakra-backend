package com.agrialert.util;

import com.agrialert.enums.District;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DistrictCoordinatesResolver {

    public record Coordinates(double latitude, double longitude) {}

    private static final Map<District, Coordinates> DISTRICT_COORDINATES = Map.of(
            District.URUBAMBA,      new Coordinates(-13.3057, -72.1174),
            District.OLLANTAYTAMBO, new Coordinates(-13.2575, -72.2647),
            District.CHINCHERO,     new Coordinates(-13.3922, -72.0578),
            District.CALCA,         new Coordinates(-13.3308, -71.9589),
            District.PISAC,         new Coordinates(-13.4208, -71.8461),
            District.ANTA,          new Coordinates(-13.4622, -72.1147),
            District.LIMATAMBO,     new Coordinates(-13.4833, -72.0500),
            District.URCOS,         new Coordinates(-13.6856, -71.6233),
            District.ANDAHUAYLILLAS,new Coordinates(-13.6722, -71.6833),
            District.OROPESA,       new Coordinates(-13.5917, -71.7667)
    );

    public Coordinates resolve(District district) {
        return DISTRICT_COORDINATES.get(district);
    }
}
