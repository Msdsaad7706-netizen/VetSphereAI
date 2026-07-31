package org.saad.vetsphereai.service;

import lombok.RequiredArgsConstructor;
import org.saad.vetsphereai.dto.AppointmentRequest;
import org.saad.vetsphereai.dto.AppointmentResponse;
import org.saad.vetsphereai.entity.Appointment;
import org.saad.vetsphereai.entity.Pet;
import org.saad.vetsphereai.entity.Veterinarian;
import org.saad.vetsphereai.repository.AppointmentRepository;
import org.saad.vetsphereai.repository.PetRepository;
import org.saad.vetsphereai.repository.VeterinarianRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PetRepository petRepository;
    private final VeterinarianRepository veterinarianRepository;

    public AppointmentResponse bookAppointment(AppointmentRequest request){
        Pet pet = petRepository.findById(request.getPetId())
                .orElseThrow(()-> new RuntimeException("Pet not found"));
        Veterinarian veterinarian = veterinarianRepository.findById(request.getVeterinarianId())
                .orElseThrow(()-> new RuntimeException("Veterinarian not found"));

        Appointment appointment = Appointment.builder()
                .pet(pet)
                .veterinarian(veterinarian)
                .appointmentDateTime(request.getAppointmentDateTime())
                .status("PENDING")
                .build();

        Appointment savedAppointment = appointmentRepository.save(appointment);
        return AppointmentResponse.builder()
                .id(savedAppointment.getId()).petId(savedAppointment.getPet().getId()).petName(savedAppointment.getPet().getName()).veterinarianId(savedAppointment.getVeterinarian().getId()).veterinarianName(savedAppointment.getVeterinarian().getFullName()).appointmentDateTime(savedAppointment.getAppointmentDateTime()).status(savedAppointment.getStatus())
                .build();
    }

}
