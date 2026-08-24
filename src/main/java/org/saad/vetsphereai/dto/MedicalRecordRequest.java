package org.saad.vetsphereai.dto;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MedicalRecordRequest {
    private Long petId;
    private Long veterinarianId;
    private String diagnosis;
    private String treatment;
    private String notes;
    private LocalDateTime recordDate;
}
