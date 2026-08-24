package org.saad.vetsphereai.dto;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MedicalRecordResponse {
    private Long id;
    private Long petId;
    private String petName;
    private  Long veterinarianId;
    private String veterinarianName;
    private String diagnosis;
    private String treatment;
    private String notes;
    private LocalDateTime recordDate;
}
