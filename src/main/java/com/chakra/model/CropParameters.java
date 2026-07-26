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
            double annualPrecipMax,
            double weightTemperature,
            double weightPrecipitation,
            double weightSoilMoisture,
            double weightElevation
    ) {}

    private static final Map<Crop, Params> PARAMS = Map.of(
            Crop.PAPA, new Params(
                    new Trapezoid(5, 17, 25, 30),
                    new Trapezoid(40, 60, 80, 95),
                    new Trapezoid(1000, 1000, 4200, 4200),
                    400, 1200,
                    0.30, 0.25, 0.35, 0.10
            ),
            Crop.MAIZ, new Params(
                    new Trapezoid(10, 22, 30, 40),
                    new Trapezoid(35, 55, 75, 90),
                    new Trapezoid(0, 0, 2500, 2500),
                    500, 800,
                    0.30, 0.30, 0.25, 0.15
            ),
            Crop.CAFE, new Params(
                    new Trapezoid(12, 18, 26, 32),
                    new Trapezoid(40, 60, 80, 90),
                    new Trapezoid(800, 800, 2000, 2000),
                    1500, 2000,
                    0.25, 0.20, 0.35, 0.20
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
