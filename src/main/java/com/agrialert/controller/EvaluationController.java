package com.agrialert.controller;

import com.agrialert.dto.request.EvaluationRequest;
import com.agrialert.dto.response.EvaluationResponse;
import com.agrialert.response.ApiResponse;
import com.agrialert.service.EvaluationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/evaluations")
@RequiredArgsConstructor
public class EvaluationController {

    private final EvaluationService evaluationService;

    @PostMapping
    public ResponseEntity<ApiResponse<EvaluationResponse>> createEvaluation(
            @Valid @RequestBody EvaluationRequest request) {

        EvaluationResponse response = evaluationService.createEvaluation(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Evaluation created successfully", response));
    }
}