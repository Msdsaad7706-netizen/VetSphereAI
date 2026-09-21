package org.saad.vetsphereai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetRiskAssessmentResponse {

    private Long petId;
    private String petName;
    private String species;
    private String breed;
    private Integer age;

    private int riskScore; // 0 - 100
    private String riskLevel; // LOW, MEDIUM, HIGH, CRITICAL

    private List<String> primaryRiskFactors;
    private List<String> recommendedInterventions;
    private int nextRecommendedCheckupDays;
    private LocalDateTime assessedAt;
}
