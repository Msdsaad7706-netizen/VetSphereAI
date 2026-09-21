package org.saad.vetsphereai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class VaccinationRequest {

    @NotNull(message = "Pet ID is required")
    private Long petId;

    @NotNull(message = "Veterinarian ID is required")
    private Long veterinarianId;

    @NotBlank(message = "Vaccine name is required")
    private String vaccineName;

    private String batchNumber;

    @NotNull(message = "Administered date is required")
    private LocalDate administeredDate;

    @NotNull(message = "Next due date is required")
    private LocalDate dueDate;

    private String notes;
}
