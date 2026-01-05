package com.studium.studium_academico.controller;

import com.studium.studium_academico.business.dto.response.AverageResult;
import com.studium.studium_academico.business.service.AverageCalculatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/averages")
@RequiredArgsConstructor
public class AverageController {

    private final AverageCalculatorService averageCalculatorService;

    @GetMapping("/calculate")
    public AverageResult calculateAverage(
            @RequestParam UUID registrationId,
            @RequestParam UUID disciplineId) {
        return averageCalculatorService.calculateAverage(registrationId, disciplineId);
    }
}