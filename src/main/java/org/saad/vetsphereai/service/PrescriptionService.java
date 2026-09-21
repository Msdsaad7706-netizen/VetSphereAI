package org.saad.vetsphereai.service;

import org.saad.vetsphereai.dto.PrescriptionRequest;
import org.saad.vetsphereai.dto.PrescriptionResponse;
import org.saad.vetsphereai.entity.Pet;
import org.saad.vetsphereai.entity.Prescription;
import org.saad.vetsphereai.entity.Veterinarian;
import org.saad.vetsphereai.exception.ResourceNotFoundException;
import org.saad.vetsphereai.repository.PetRepository;
import org.saad.vetsphereai.repository.PrescriptionRepository;
import org.saad.vetsphereai.repository.VeterinarianRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PetRepository petRepository;
    private final VeterinarianRepository veterinarianRepository;

    public PrescriptionService(
            PrescriptionRepository prescriptionRepository,
            PetRepository petRepository,
            VeterinarianRepository veterinarianRepository) {

        this.prescriptionRepository = prescriptionRepository;
        this.petRepository = petRepository;
        this.veterinarianRepository = veterinarianRepository;
    }

    @Transactional
    public PrescriptionResponse createPrescription(PrescriptionRequest request) {

        Pet pet = petRepository.findById(request.getPetId())
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with ID: " + request.getPetId()));

        Veterinarian veterinarian = veterinarianRepository.findById(request.getVeterinarianId())
                .orElseThrow(() -> new ResourceNotFoundException("Veterinarian not found with ID: " + request.getVeterinarianId()));

        Prescription prescription = new Prescription();
        prescription.setPet(pet);
        prescription.setVeterinarian(veterinarian);
        prescription.setMedicineName(request.getMedicineName());
        prescription.setDosage(request.getDosage());
        prescription.setFrequency(request.getFrequency());
        prescription.setDuration(request.getDuration());
        prescription.setInstructions(request.getInstructions());
        prescription.setStatus(request.getStatus() != null ? request.getStatus() : "ACTIVE");
        prescription.setPrescriptionDate(request.getPrescriptionDate() != null ? request.getPrescriptionDate() : LocalDateTime.now());

        Prescription savedPrescription = prescriptionRepository.save(prescription);
        return mapToResponse(savedPrescription);
    }

    @Transactional(readOnly = true)
    public List<PrescriptionResponse> getAllPrescriptions() {
        return prescriptionRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PrescriptionResponse> getPrescriptionsByPetId(Long petId) {
        if (!petRepository.existsById(petId)) {
            throw new ResourceNotFoundException("Pet not found with ID: " + petId);
        }
        return prescriptionRepository.findByPetId(petId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PrescriptionResponse getPrescriptionById(Long id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found with ID: " + id));

        return mapToResponse(prescription);
    }

    @Transactional
    public PrescriptionResponse updatePrescription(Long id, PrescriptionRequest request) {

        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found with ID: " + id));

        Pet pet = petRepository.findById(request.getPetId())
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with ID: " + request.getPetId()));

        Veterinarian veterinarian = veterinarianRepository.findById(request.getVeterinarianId())
                .orElseThrow(() -> new ResourceNotFoundException("Veterinarian not found with ID: " + request.getVeterinarianId()));

        prescription.setPet(pet);
        prescription.setVeterinarian(veterinarian);
        prescription.setMedicineName(request.getMedicineName());
        prescription.setDosage(request.getDosage());
        prescription.setFrequency(request.getFrequency());
        prescription.setDuration(request.getDuration());
        prescription.setInstructions(request.getInstructions());
        if (request.getStatus() != null) {
            prescription.setStatus(request.getStatus());
        }
        if (request.getPrescriptionDate() != null) {
            prescription.setPrescriptionDate(request.getPrescriptionDate());
        }

        Prescription updatedPrescription = prescriptionRepository.save(prescription);
        return mapToResponse(updatedPrescription);
    }

    @Transactional
    public void deletePrescription(Long id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found with ID: " + id));

        prescriptionRepository.delete(prescription);
    }

    private PrescriptionResponse mapToResponse(Prescription prescription) {
        return PrescriptionResponse.builder()
                .id(prescription.getId())
                .petId(prescription.getPet().getId())
                .petName(prescription.getPet().getName())
                .veterinarianId(prescription.getVeterinarian().getId())
                .veterinarianName(prescription.getVeterinarian().getFullName())
                .medicineName(prescription.getMedicineName())
                .dosage(prescription.getDosage())
                .frequency(prescription.getFrequency())
                .duration(prescription.getDuration())
                .instructions(prescription.getInstructions())
                .status(prescription.getStatus())
                .prescriptionDate(prescription.getPrescriptionDate())
                .build();
    }
}