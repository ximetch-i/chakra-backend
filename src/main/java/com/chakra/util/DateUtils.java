package com.chakra.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static String formatDate(LocalDate date) {
        return date.format(FORMATTER);
    }
    public static LocalDate today() {
        return LocalDate.now();
    }
}
