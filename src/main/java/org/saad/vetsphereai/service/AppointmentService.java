package org.saad.vetsphereai.service;

import org.saad.vetsphereai.dto.AppointmentRequest;
import org.saad.vetsphereai.dto.AppointmentResponse;
import org.saad.vetsphereai.dto.AppointmentStatusRequest;
import org.saad.vetsphereai.entity.Appointment;
import org.saad.vetsphereai.entity.AppointmentStatus;
import org.saad.vetsphereai.entity.Pet;
import org.saad.vetsphereai.entity.Veterinarian;
import org.saad.vetsphereai.exception.BadRequestException;
import org.saad.vetsphereai.exception.ResourceNotFoundException;
import org.saad.vetsphereai.repository.AppointmentRepository;
import org.saad.vetsphereai.repository.PetRepository;
import org.saad.vetsphereai.repository.VeterinarianRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PetRepository petRepository;
    private final VeterinarianRepository veterinarianRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PetRepository petRepository,
            VeterinarianRepository veterinarianRepository) {

        this.appointmentRepository = appointmentRepository;
        this.petRepository = petRepository;
        this.veterinarianRepository = veterinarianRepository;
    }

    @Transactional
    public AppointmentResponse bookAppointment(AppointmentRequest request) {

        Pet pet = petRepository.findById(request.getPetId())
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with ID: " + request.getPetId()));

        Veterinarian veterinarian = veterinarianRepository.findById(request.getVeterinarianId())
                .orElseThrow(() -> new ResourceNotFoundException("Veterinarian not found with ID: " + request.getVeterinarianId()));

        Appointment appointment = new Appointment();
        appointment.setPet(pet);
        appointment.setVeterinarian(veterinarian);
        appointment.setAppointmentDateTime(request.getAppointmentDateTime());
        appointment.setStatus(AppointmentStatus.PENDING);

        Appointment savedAppointment = appointmentRepository.save(appointment);
        return mapToResponse(savedAppointment);
    }

    @Transactional(readOnly = true)
    public AppointmentResponse getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + id));
        return mapToResponse(appointment);
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public AppointmentResponse updateAppointmentStatus(Long id, AppointmentStatusRequest request) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + id));

        AppointmentStatus status;
        try {
            status = AppointmentStatus.valueOf(request.getStatus().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new BadRequestException("Invalid appointment status: " + request.getStatus());
        }

        appointment.setStatus(status);
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        return mapToResponse(updatedAppointment);
    }

    @Transactional
    public void deleteAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + id));
        appointmentRepository.delete(appointment);
    }

    private AppointmentResponse mapToResponse(Appointment appointment) {
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .petId(appointment.getPet().getId())
                .petName(appointment.getPet().getName())
                .veterinarianId(appointment.getVeterinarian().getId())
                .veterinarianName(appointment.getVeterinarian().getFullName())
                .appointmentDateTime(appointment.getAppointmentDateTime())
                .status(appointment.getStatus() != null ? appointment.getStatus().name() : "PENDING")
                .build();
    }
}