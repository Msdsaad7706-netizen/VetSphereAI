package org.saad.vetsphereai.service;

import org.saad.vetsphereai.dto.DashboardStatsResponse;
import org.saad.vetsphereai.entity.AppointmentStatus;
import org.saad.vetsphereai.entity.Pet;
import org.saad.vetsphereai.entity.VaccinationStatus;
import org.saad.vetsphereai.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final PetRepository petRepository;
    private final VeterinarianRepository veterinarianRepository;
    private final AppointmentRepository appointmentRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final VaccinationRepository vaccinationRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    public DashboardService(PetRepository petRepository,
                            VeterinarianRepository veterinarianRepository,
                            AppointmentRepository appointmentRepository,
                            PrescriptionRepository prescriptionRepository,
                            VaccinationRepository vaccinationRepository,
                            MedicalRecordRepository medicalRecordRepository) {
        this.petRepository = petRepository;
        this.veterinarianRepository = veterinarianRepository;
        this.appointmentRepository = appointmentRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.vaccinationRepository = vaccinationRepository;
        this.medicalRecordRepository = medicalRecordRepository;
    }

    @Transactional(readOnly = true)
    public DashboardStatsResponse getDashboardStats() {
        long totalPets = petRepository.count();
        long totalVets = veterinarianRepository.count();
        long totalAppointments = appointmentRepository.count();

        long pendingAppointments = appointmentRepository.countByStatus(AppointmentStatus.PENDING);
        long confirmedAppointments = appointmentRepository.countByStatus(AppointmentStatus.CONFIRMED);
        long completedAppointments = appointmentRepository.countByStatus(AppointmentStatus.COMPLETED);
        long cancelledAppointments = appointmentRepository.countByStatus(AppointmentStatus.CANCELLED);

        long totalPrescriptions = prescriptionRepository.count();
        long activePrescriptions = prescriptionRepository.countByStatus("ACTIVE");

        long totalVaccinations = vaccinationRepository.count();
        long vaccinationsDueSoon = vaccinationRepository.countByStatus(VaccinationStatus.DUE_SOON);
        long vaccinationsOverdue = vaccinationRepository.countByStatus(VaccinationStatus.OVERDUE);

        long totalMedicalRecords = medicalRecordRepository.count();

        // Species breakdown
        Map<String, Long> petsBySpecies = petRepository.findAll().stream()
                .filter(p -> p.getSpecies() != null && !p.getSpecies().isBlank())
                .collect(Collectors.groupingBy(Pet::getSpecies, Collectors.counting()));

        // Appointment breakdown
        Map<String, Long> appointmentStatusDistribution = new HashMap<>();
        appointmentStatusDistribution.put("PENDING", pendingAppointments);
        appointmentStatusDistribution.put("CONFIRMED", confirmedAppointments);
        appointmentStatusDistribution.put("COMPLETED", completedAppointments);
        appointmentStatusDistribution.put("CANCELLED", cancelledAppointments);

        return DashboardStatsResponse.builder()
                .totalPets(totalPets)
                .totalVeterinarians(totalVets)
                .totalAppointments(totalAppointments)
                .pendingAppointments(pendingAppointments)
                .confirmedAppointments(confirmedAppointments)
                .completedAppointments(completedAppointments)
                .cancelledAppointments(cancelledAppointments)
                .totalPrescriptions(totalPrescriptions)
                .activePrescriptions(activePrescriptions)
                .totalVaccinations(totalVaccinations)
                .vaccinationsDueSoon(vaccinationsDueSoon)
                .vaccinationsOverdue(vaccinationsOverdue)
                .totalMedicalRecords(totalMedicalRecords)
                .petsBySpecies(petsBySpecies)
                .appointmentStatusDistribution(appointmentStatusDistribution)
                .build();
    }
}
