package org.saad.vetsphereai.controller;

import org.saad.vetsphereai.dto.VeterinarianRequest;
import org.saad.vetsphereai.dto.VeterinarianResponse;
import org.saad.vetsphereai.service.VeterinarianService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/veterinarians")
public class VeterinarianController {

    private final VeterinarianService veterinarianService;

    public VeterinarianController(VeterinarianService veterinarianService) {
        this.veterinarianService = veterinarianService;
    }

    @PostMapping
    public VeterinarianResponse addVeterinarian(
            @RequestBody VeterinarianRequest request) {
        return veterinarianService.addVeterinarian(request);
    }

    @GetMapping
    public List<VeterinarianResponse> getAllVeterinarians() {
        return veterinarianService.getAllVeterinarians();
    }

    @GetMapping("/{id}")
    public VeterinarianResponse getVeterinarianById(@PathVariable Long id) {
        return veterinarianService.getVeterinarianById(id);
    }

    @PutMapping("/{id}")
    public VeterinarianResponse updateVeterinarian(
            @PathVariable Long id,
            @RequestBody VeterinarianRequest request) {
        return veterinarianService.updateVeterinarian(id, request);
    }


    @DeleteMapping("/{id}")
    public String deleteVeterinarian(@PathVariable Long id) {
        veterinarianService.deleteVeterinarian(id);
        return "Veterinarian deleted successfully";
    }
}