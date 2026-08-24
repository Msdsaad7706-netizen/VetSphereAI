package org.saad.vetsphereai.service;

import org.saad.vetsphereai.dto.AppointmentRequest;
import org.saad.vetsphereai.dto.AppointmentResponse;
import org.saad.vetsphereai.dto.AppointmentStatusRequest;
import org.saad.vetsphereai.entity.Appointment;
import org.saad.vetsphereai.entity.Pet;
import org.saad.vetsphereai.entity.AppointmentStatus;
import org.saad.vetsphereai.entity.Veterinarian;
import org.saad.vetsphereai.repository.AppointmentRepository;
import org.saad.vetsphereai.repository.PetRepository;
import org.saad.vetsphereai.repository.VeterinarianRepository;
import org.springframework.stereotype.Service;

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

    public AppointmentResponse bookAppointment(AppointmentRequest request) {

        Pet pet = petRepository.findById(request.getPetId())
                .orElseThrow(() -> new RuntimeException("Pet not found"));

        Veterinarian veterinarian = veterinarianRepository.findById(request.getVeterinarianId())
                .orElseThrow(() -> new RuntimeException("Veterinarian not found"));

        Appointment appointment = new Appointment();

        appointment.setPet(pet);
        appointment.setVeterinarian(veterinarian);
        appointment.setAppointmentDateTime(request.getAppointmentDateTime());
        appointment.setStatus(AppointmentStatus.PENDING);

        Appointment savedAppointment = appointmentRepository.save(appointment);

        return AppointmentResponse.builder()
                .id(savedAppointment.getId())
                .petId(savedAppointment.getPet().getId())
                .petName(savedAppointment.getPet().getName())
                .veterinarianId(savedAppointment.getVeterinarian().getId())
                .veterinarianName(savedAppointment.getVeterinarian().getFullName())
                .appointmentDateTime(savedAppointment.getAppointmentDateTime())
                .status(savedAppointment.getStatus())
                .build();
    }

    public AppointmentResponse getAppointmentById(Long id){
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(()-> new RuntimeException("Appointment not found"));
        return
                AppointmentResponse.builder()
                        .id(appointment.getId())
                        .petId(appointment.getPet().getId())
                        .petName(appointment.getPet().getName())
                        .veterinarianId(appointment.getVeterinarian().getId())
                        .veterinarianName(appointment.getVeterinarian().getFullName())
                        .appointmentDateTime(appointment.getAppointmentDateTime())
                        .status(appointment.getStatus())
                        .build();
    }
    public List<AppointmentResponse> getAllAppointments(){
        return
                appointmentRepository.findAll()
                        .stream()
                        .map(appointment -> AppointmentResponse.builder()
                                .id(appointment.getId())
                                .petId(appointment.getPet().getId())
                                .petName(appointment.getPet().getName())
                                .veterinarianId(appointment.getVeterinarian().getId())
                                .veterinarianName(appointment.getVeterinarian().getFullName())
                                .appointmentDateTime(appointment.getAppointmentDateTime())
                                .status(appointment.getStatus())
                                .build())
                        .toList();
    }
    
    public AppointmentResponse updateAppointmentStatus(Long id, AppointmentStatusRequest request){
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(()-> new RuntimeException("Appointment not found"));
//        appointment.setStatus(request.getStatus());

        AppointmentStatus status;
        try{
            status =AppointmentStatus.valueOf(request.getStatus().toUpperCase());
        } catch (IllegalArgumentException e)
        {
            throw new RuntimeException("Invalid appointment status");
        }

        appointment.setStatus(status);
        
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        return AppointmentResponse.builder()
                .id(updatedAppointment.getId()).petId(updatedAppointment.getPet().getId()).petName(updatedAppointment.getPet().getName()).veterinarianId(updatedAppointment.getVeterinarian().getId()).veterinarianName(updatedAppointment.getVeterinarian().getFullName()).appointmentDateTime(updatedAppointment.getAppointmentDateTime()).status(updatedAppointment.getStatus())
                .build();
    }

    public void deleteAppointment(Long id){
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(()-> new RuntimeException("Appointment not found"));
        appointmentRepository.delete(appointment);
    }
}