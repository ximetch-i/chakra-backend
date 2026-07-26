package com.chakra.model;

import com.chakra.enums.Crop;

import java.util.Map;

public class CropParameters {

    public record Trapezoid(double a, double b, double c, double d) {}

    public record Params(
            Trapezoid temperature,
            Trapezoid soilMoisture,
            Trapezoid elevation,
            double annualPrecipMin,
            double annualPrecipMax
    ) {}

    public static final double WEIGHT_TEMPERATURE = 0.263;
    public static final double WEIGHT_PRECIPITATION = 0.558;
    public static final double WEIGHT_SOIL_MOISTURE = 0.122;
    public static final double WEIGHT_ELEVATION = 0.057;

    private static final Map<Crop, Params> PARAMS = Map.of(
            Crop.PAPA, new Params(
                    new Trapezoid(5, 17, 25, 30),
                    new Trapezoid(40, 60, 80, 95),
                    new Trapezoid(1000, 1000, 4200, 4200),
                    400, 1200
            ),
            Crop.MAIZ, new Params(
                    new Trapezoid(10, 15, 25, 30),
                    new Trapezoid(40, 60, 80, 95),
                    new Trapezoid(1500, 1500, 3800, 3800),
                    500, 800
            ),
            Crop.CAFE, new Params(
                    new Trapezoid(15, 18, 22, 25),
                    new Trapezoid(40, 60, 80, 90),
                    new Trapezoid(800, 1200, 2600, 2600),
                    1500, 2000
            )
    );

    public static Params of(Crop crop) {
        Params params = PARAMS.get(crop);
        if (params == null) {
            throw new IllegalArgumentException("No parameters defined for crop: " + crop);
        }
        return params;
    }
}
