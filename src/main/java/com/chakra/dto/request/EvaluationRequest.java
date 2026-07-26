package com.chakra.dto.request;

import com.chakra.enums.Crop;
import com.chakra.enums.District;
import com.chakra.validation.TodayOrFuture;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationRequest {

    @NotNull(message = "District is required")
    private District district;

    @NotNull(message = "Crop is required")
    private Crop crop;

    @NotNull(message = "Planting date is required")
    @TodayOrFuture
    private LocalDate plantingDate;
}