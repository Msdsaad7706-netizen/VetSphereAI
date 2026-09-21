package org.saad.vetsphereai.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AiHealthAdvisorRequest {

    private String petName;

    @NotBlank(message = "Species is required (e.g., Dog, Cat, Bird)")
    private String species;

    private String breed;
    private Integer age;
    private Double weightKg;

    @NotBlank(message = "Symptoms description is required")
    private String symptoms;

    private String medicalHistory;
    private String userQuery;
}
