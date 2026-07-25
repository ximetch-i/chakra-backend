package com.agrialert.service;

import com.agrialert.dto.request.EvaluationRequest;
import com.agrialert.dto.response.EvaluationResponse;

public interface EvaluationService {
    EvaluationResponse createEvaluation(EvaluationRequest request);
}