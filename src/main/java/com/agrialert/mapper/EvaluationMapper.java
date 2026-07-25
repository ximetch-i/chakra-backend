package com.agrialert.mapper;

import com.agrialert.dto.request.EvaluationRequest;
import com.agrialert.dto.response.EvaluationResponse;
import com.agrialert.entity.Evaluation;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EvaluationMapper {

    private final ModelMapper modelMapper;

    public Evaluation toEntity(EvaluationRequest request) {
        return modelMapper.map(request, Evaluation.class);
    }

    public EvaluationResponse toResponse(Evaluation evaluation) {
        return modelMapper.map(evaluation, EvaluationResponse.class);
    }
}
