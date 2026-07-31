package org.saad.vetsphereai.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentRequest {
    private Long petId;

    private Long veterinarianId;

    private LocalDateTime appointmentDateTime;
}
