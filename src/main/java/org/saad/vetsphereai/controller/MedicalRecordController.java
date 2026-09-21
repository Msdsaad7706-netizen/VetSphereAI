package org.saad.vetsphereai.controller;

import org.saad.vetsphereai.dto.MedicalRecordRequest;
import org.saad.vetsphereai.dto.MedicalRecordResponse;
import org.saad.vetsphereai.service.MedicalRecordService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medical-records")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    // CREATE
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MedicalRecordResponse createMedicalRecord(
            @RequestBody MedicalRecordRequest request) {

        return medicalRecordService.createMedicalRecord(request);
    }

    // GET ALL
    @GetMapping
    public List<MedicalRecordResponse> getAllMedicalRecords() {
        return medicalRecordService.getAllMedicalRecords();
    }

    // GET BY ID
    @GetMapping("/{id}")
    public MedicalRecordResponse getMedicalRecordById(
            @PathVariable Long id) {

        return medicalRecordService.getMedicalRecordById(id);
    }

    // UPDATE
    @PutMapping("/{id}")
    public MedicalRecordResponse updateMedicalRecord(
            @PathVariable Long id,
            @RequestBody MedicalRecordRequest request) {

        return medicalRecordService.updateMedicalRecord(id, request);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public String deleteMedicalRecord(@PathVariable Long id) {

        medicalRecordService.deleteMedicalRecord(id);
        return "Medical record deleted successfully";
    }
}