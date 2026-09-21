package org.saad.vetsphereai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.saad.vetsphereai.dto.PetRiskAssessmentResponse;
import org.saad.vetsphereai.service.PetHealthMlService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ml")
@Tag(name = "ML Health Risk Engine", description = "Machine learning & heuristic pet risk score calculation endpoints")
public class MlController {

    private final PetHealthMlService petHealthMlService;

    public MlController(PetHealthMlService petHealthMlService) {
        this.petHealthMlService = petHealthMlService;
    }

    @GetMapping("/pet-risk/{petId}")
    @Operation(summary = "Calculate Pet Health Risk Score", description = "Generates a 0-100 ML health risk score, risk level (LOW/MEDIUM/HIGH/CRITICAL), and risk factors for a pet")
    public PetRiskAssessmentResponse getPetHealthRisk(@PathVariable Long petId) {
        return petHealthMlService.calculatePetHealthRisk(petId);
    }
}
