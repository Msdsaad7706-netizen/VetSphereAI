package org.saad.vetsphereai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.saad.vetsphereai.dto.AiHealthAdvisorRequest;
import org.saad.vetsphereai.dto.AiHealthAdvisorResponse;
import org.saad.vetsphereai.service.AiHealthAdvisorService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@Tag(name = "Spring AI Health Advisor", description = "AI-powered pet symptom checker, clinical triage, and dosage assistant endpoints")
public class AiController {

    private final AiHealthAdvisorService aiHealthAdvisorService;

    public AiController(AiHealthAdvisorService aiHealthAdvisorService) {
        this.aiHealthAdvisorService = aiHealthAdvisorService;
    }

    @PostMapping("/health-advice")
    @Operation(summary = "AI Pet Health Check & Triage", description = "Evaluates pet symptoms, species factors, and provides AI-driven clinical triage and care recommendations")
    public AiHealthAdvisorResponse getHealthAdvice(@Valid @RequestBody AiHealthAdvisorRequest request) {
        return aiHealthAdvisorService.generateHealthAdvice(request);
    }
}
