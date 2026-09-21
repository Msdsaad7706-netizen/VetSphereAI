package org.saad.vetsphereai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionResponse {

    private Long id;

    private Long petId;
    private String petName;

    private Long veterinarianId;
    private String veterinarianName;

    private String medicineName;
    private String dosage;
    private String frequency;
    private String duration;
    private String instructions;
    private String status;

    private LocalDateTime prescriptionDate;
}