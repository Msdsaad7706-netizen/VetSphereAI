package org.saad.vetsphereai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
//@NoArgsConstructor
//@AllArgsConstructor
@Builder
public class AppointmentResponse {
    private Long id;
    private Long petId;
    private String petName;
    private Long veterinarianId;
    private String veterinarianName;
    private LocalDateTime appointmentDateTime;
    private String status;
}
