package org.saad.vetsphereai.controller;

import org.saad.vetsphereai.dto.AppointmentRequest;
import org.saad.vetsphereai.dto.AppointmentResponse;
import org.saad.vetsphereai.dto.AppointmentStatusRequest;
import org.saad.vetsphereai.service.AppointmentService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }
    @PreAuthorize("hasRole('PET_OWNER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponse bookAppointment(
            @RequestBody AppointmentRequest request) {

        return appointmentService.bookAppointment(request);
    }

    @PreAuthorize("hasAnyRole('PET_OWNER', 'VETERINARIAN', 'ADMIN')")
    @GetMapping("/{id}")
    public  AppointmentResponse getAppointmentById(@PathVariable Long id){
        return
                appointmentService.getAppointmentById(id);
    }

    @PreAuthorize("hasAnyRole('PET_OWNER', 'VETERINARIAN', 'ADMIN')")
    @GetMapping
    public List<AppointmentResponse> getAllAppointments(){
        return appointmentService.getAllAppointments();
    }

    @PreAuthorize("hasAnyRole('VETERINARIAN', 'ADMIN')")
    @PutMapping("/{id}/status")
    public  AppointmentResponse updatedAppointmentStatus(@PathVariable Long id, @RequestBody AppointmentStatusRequest request) {
        return  appointmentService.updateAppointmentStatus(id, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public String deleteAppointment(@PathVariable Long id){
        appointmentService.deleteAppointment(id);
        return  "Appointment deleted succesfully";
    }
}