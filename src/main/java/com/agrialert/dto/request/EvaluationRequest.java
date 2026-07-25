package com.agrialert.dto.request;

import com.agrialert.enums.Crop;
import com.agrialert.enums.District;
import jakarta.validation.constraints.Future;
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

    @NotNull(message = "El distrito es obligatorio")
    private District district;

    @NotNull(message = "El cultivo es obligatorio")
    private Crop crop;

    @NotNull(message = "La fecha de siembra es obligatoria")
    @Future(message = "La fecha de siembra debe ser una fecha futura")
    private LocalDate plantingDate;
}