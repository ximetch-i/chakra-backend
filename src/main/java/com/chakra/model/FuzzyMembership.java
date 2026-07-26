package com.chakra.model;

public class FuzzyMembership {

    private FuzzyMembership() {}

    /**
     * Trapezoidal membership function.
     *
     * f(x) = 0                          if x <= a
     *         100 * (x - a) / (b - a)   if a < x < b
     *         100                        if b <= x <= c
     *         100 * (d - x) / (d - c)   if c < x < d
     *         0                          if x >= d
     */
    public static double trapezoidal(double x, double a, double b, double c, double d) {
        if (x <= a || x >= d) {
            return 0.0;
        }
        if (x >= b && x <= c) {
            return 100.0;
        }
        if (x > a && x < b) {
            return 100.0 * (x - a) / (b - a);
        }
        // c < x < d
        return 100.0 * (d - x) / (d - c);
    }
}
