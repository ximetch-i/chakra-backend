package com.chakra.mapper;

import com.chakra.dto.request.EvaluationRequest;
import com.chakra.dto.response.EvaluationResponse;
import com.chakra.entity.Evaluation;
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
