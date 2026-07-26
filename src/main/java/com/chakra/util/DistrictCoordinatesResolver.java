package com.chakra.util;

import com.chakra.enums.District;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DistrictCoordinatesResolver {

    public record Coordinates(double latitude, double longitude, double elevation) {}

    private static final Map<District, Coordinates> DISTRICT_COORDINATES = Map.of(
            District.URUBAMBA,      new Coordinates(-13.3057, -72.1174, 2870),
            District.OLLANTAYTAMBO, new Coordinates(-13.2575, -72.2647, 2810),
            District.CHINCHERO,     new Coordinates(-13.3922, -72.0578, 3762),
            District.CALCA,         new Coordinates(-13.3308, -71.9589, 2926),
            District.PISAC,         new Coordinates(-13.4208, -71.8461, 2972),
            District.ANTA,          new Coordinates(-13.4717, -72.1489, 3400),
            District.LIMATAMBO,     new Coordinates(-13.4794, -72.4442, 2711),
            District.URCOS,         new Coordinates(-13.6856, -71.6233, 3161),
            District.ANDAHUAYLILLAS,new Coordinates(-13.6722, -71.6833, 3163),
            District.OROPESA,       new Coordinates(-13.5917, -71.7667, 3157)
    );

    public Coordinates resolve(District district) {
        return DISTRICT_COORDINATES.get(district);
    }
}
