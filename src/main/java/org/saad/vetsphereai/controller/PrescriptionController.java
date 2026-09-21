package org.saad.vetsphereai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.saad.vetsphereai.dto.PrescriptionRequest;
import org.saad.vetsphereai.dto.PrescriptionResponse;
import org.saad.vetsphereai.service.PrescriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
@Tag(name = "Prescriptions", description = "Prescription management endpoints")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new prescription", description = "Issues a new medical prescription for a pet")
    public PrescriptionResponse createPrescription(@Valid @RequestBody PrescriptionRequest request) {
        return prescriptionService.createPrescription(request);
    }

    @GetMapping
    @Operation(summary = "Get all prescriptions", description = "Retrieves all issued prescriptions")
    public List<PrescriptionResponse> getAllPrescriptions() {
        return prescriptionService.getAllPrescriptions();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get prescription by ID", description = "Retrieves details of a specific prescription")
    public PrescriptionResponse getPrescriptionById(@PathVariable Long id) {
        return prescriptionService.getPrescriptionById(id);
    }

    @GetMapping("/pet/{petId}")
    @Operation(summary = "Get prescriptions by Pet ID", description = "Retrieves all prescriptions for a specific pet")
    public List<PrescriptionResponse> getPrescriptionsByPetId(@PathVariable Long petId) {
        return prescriptionService.getPrescriptionsByPetId(petId);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update prescription", description = "Updates an existing prescription record")
    public PrescriptionResponse updatePrescription(
            @PathVariable Long id,
            @Valid @RequestBody PrescriptionRequest request) {
        return prescriptionService.updatePrescription(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete prescription", description = "Removes a prescription record")
    public String deletePrescription(@PathVariable Long id) {
        prescriptionService.deletePrescription(id);
        return "Prescription deleted successfully";
    }
}