package com.chakra.service;

import com.chakra.dto.request.EvaluationRequest;
import com.chakra.dto.response.EvaluationResponse;

public interface EvaluationService {
    EvaluationResponse createEvaluation(EvaluationRequest request);
}