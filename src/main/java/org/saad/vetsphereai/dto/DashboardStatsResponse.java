package org.saad.vetsphereai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {

    private long totalPets;
    private long totalVeterinarians;
    private long totalAppointments;
    private long pendingAppointments;
    private long confirmedAppointments;
    private long completedAppointments;
    private long cancelledAppointments;
    private long totalPrescriptions;
    private long activePrescriptions;
    private long totalVaccinations;
    private long vaccinationsDueSoon;
    private long vaccinationsOverdue;
    private long totalMedicalRecords;

    private Map<String, Long> petsBySpecies;
    private Map<String, Long> appointmentStatusDistribution;
}
