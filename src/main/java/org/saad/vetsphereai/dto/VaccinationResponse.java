package org.saad.vetsphereai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.saad.vetsphereai.entity.VaccinationStatus;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VaccinationResponse {

    private Long id;

    private Long petId;
    private String petName;
    private String petSpecies;
    private String petBreed;

    private Long veterinarianId;
    private String veterinarianName;

    private String vaccineName;
    private String batchNumber;
    private LocalDate administeredDate;
    private LocalDate dueDate;
    private VaccinationStatus status;
    private String notes;
}
