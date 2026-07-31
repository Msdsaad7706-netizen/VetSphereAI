package org.saad.vetsphereai.controller;

import lombok.RequiredArgsConstructor;
import org.saad.vetsphereai.dto.AppointmentRequest;
import org.saad.vetsphereai.dto.AppointmentResponse;
import org.saad.vetsphereai.service.AppointmentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponse bookAppointment(@RequestBody AppointmentRequest request){
        return appointmentService.bookAppointment(request);
    }
    @GetMapping("/test")
    public String test() {
        return "Appointment API Working";
    }

}
