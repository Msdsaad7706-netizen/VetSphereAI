package org.saad.vetsphereai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.saad.vetsphereai.dto.VaccinationRequest;
import org.saad.vetsphereai.dto.VaccinationResponse;
import org.saad.vetsphereai.service.VaccinationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vaccinations")
@Tag(name = "Vaccinations", description = "Pet vaccination schedule and tracking management endpoints")
public class VaccinationController {

    private final VaccinationService vaccinationService;

    public VaccinationController(VaccinationService vaccinationService) {
        this.vaccinationService = vaccinationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Record a vaccination", description = "Administers and records a new vaccination entry for a pet")
    public VaccinationResponse createVaccination(@Valid @RequestBody VaccinationRequest request) {
        return vaccinationService.createVaccination(request);
    }

    @GetMapping
    @Operation(summary = "Get all vaccination records", description = "Retrieves all vaccination records across the clinic")
    public List<VaccinationResponse> getAllVaccinations() {
        return vaccinationService.getAllVaccinations();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get vaccination record by ID", description = "Retrieves details of a specific vaccination record")
    public VaccinationResponse getVaccinationById(@PathVariable Long id) {
        return vaccinationService.getVaccinationById(id);
    }

    @GetMapping("/pet/{petId}")
    @Operation(summary = "Get vaccinations by Pet ID", description = "Retrieves complete vaccination history for a pet")
    public List<VaccinationResponse> getVaccinationsByPetId(@PathVariable Long petId) {
        return vaccinationService.getVaccinationsByPetId(petId);
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue vaccinations", description = "Retrieves all pet vaccinations that are overdue")
    public List<VaccinationResponse> getOverdueVaccinations() {
        return vaccinationService.getOverdueVaccinations();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update vaccination record", description = "Updates an existing vaccination record")
    public VaccinationResponse updateVaccination(
            @PathVariable Long id,
            @Valid @RequestBody VaccinationRequest request) {
        return vaccinationService.updateVaccination(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete vaccination record", description = "Removes a vaccination record")
    public String deleteVaccination(@PathVariable Long id) {
        vaccinationService.deleteVaccination(id);
        return "Vaccination record deleted successfully";
    }
}
