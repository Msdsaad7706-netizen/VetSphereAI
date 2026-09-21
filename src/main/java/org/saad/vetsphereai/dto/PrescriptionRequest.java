package org.saad.vetsphereai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PrescriptionRequest {

    @NotNull(message = "Pet ID is required")
    private Long petId;

    @NotNull(message = "Veterinarian ID is required")
    private Long veterinarianId;

    @NotBlank(message = "Medicine name is required")
    private String medicineName;

    @NotBlank(message = "Dosage is required")
    private String dosage;

    @NotBlank(message = "Frequency is required")
    private String frequency;

    @NotBlank(message = "Duration is required")
    private String duration;

    private String instructions;

    private String status; // ACTIVE, COMPLETED, DISCONTINUED

    private LocalDateTime prescriptionDate;
}