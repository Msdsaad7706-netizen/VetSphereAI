package org.saad.vetsphereai.service;

import org.saad.vetsphereai.dto.VaccinationRequest;
import org.saad.vetsphereai.dto.VaccinationResponse;
import org.saad.vetsphereai.entity.Pet;
import org.saad.vetsphereai.entity.VaccinationRecord;
import org.saad.vetsphereai.entity.VaccinationStatus;
import org.saad.vetsphereai.entity.Veterinarian;
import org.saad.vetsphereai.exception.ResourceNotFoundException;
import org.saad.vetsphereai.repository.PetRepository;
import org.saad.vetsphereai.repository.VaccinationRepository;
import org.saad.vetsphereai.repository.VeterinarianRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class VaccinationService {

    private final VaccinationRepository vaccinationRepository;
    private final PetRepository petRepository;
    private final VeterinarianRepository veterinarianRepository;

    public VaccinationService(VaccinationRepository vaccinationRepository,
                              PetRepository petRepository,
                              VeterinarianRepository veterinarianRepository) {
        this.vaccinationRepository = vaccinationRepository;
        this.petRepository = petRepository;
        this.veterinarianRepository = veterinarianRepository;
    }

    @Transactional
    public VaccinationResponse createVaccination(VaccinationRequest request) {
        Pet pet = petRepository.findById(request.getPetId())
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with ID: " + request.getPetId()));

        Veterinarian vet = veterinarianRepository.findById(request.getVeterinarianId())
                .orElseThrow(() -> new ResourceNotFoundException("Veterinarian not found with ID: " + request.getVeterinarianId()));

        VaccinationRecord record = new VaccinationRecord();
        record.setPet(pet);
        record.setVeterinarian(vet);
        record.setVaccineName(request.getVaccineName());
        record.setBatchNumber(request.getBatchNumber());
        record.setAdministeredDate(request.getAdministeredDate());
        record.setDueDate(request.getDueDate());
        record.setNotes(request.getNotes());
        record.setStatus(computeStatus(request.getDueDate()));

        VaccinationRecord saved = vaccinationRepository.save(record);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<VaccinationResponse> getAllVaccinations() {
        return vaccinationRepository.findAll().stream()
                .peek(this::refreshStatusIfNeeded)
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public VaccinationResponse getVaccinationById(Long id) {
        VaccinationRecord record = vaccinationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vaccination record not found with ID: " + id));
        refreshStatusIfNeeded(record);
        return mapToResponse(record);
    }

    @Transactional(readOnly = true)
    public List<VaccinationResponse> getVaccinationsByPetId(Long petId) {
        if (!petRepository.existsById(petId)) {
            throw new ResourceNotFoundException("Pet not found with ID: " + petId);
        }
        return vaccinationRepository.findByPetId(petId).stream()
                .peek(this::refreshStatusIfNeeded)
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VaccinationResponse> getOverdueVaccinations() {
        return vaccinationRepository.findByStatus(VaccinationStatus.OVERDUE).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public VaccinationResponse updateVaccination(Long id, VaccinationRequest request) {
        VaccinationRecord record = vaccinationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vaccination record not found with ID: " + id));

        Pet pet = petRepository.findById(request.getPetId())
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with ID: " + request.getPetId()));

        Veterinarian vet = veterinarianRepository.findById(request.getVeterinarianId())
                .orElseThrow(() -> new ResourceNotFoundException("Veterinarian not found with ID: " + request.getVeterinarianId()));

        record.setPet(pet);
        record.setVeterinarian(vet);
        record.setVaccineName(request.getVaccineName());
        record.setBatchNumber(request.getBatchNumber());
        record.setAdministeredDate(request.getAdministeredDate());
        record.setDueDate(request.getDueDate());
        record.setNotes(request.getNotes());
        record.setStatus(computeStatus(request.getDueDate()));

        VaccinationRecord updated = vaccinationRepository.save(record);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteVaccination(Long id) {
        VaccinationRecord record = vaccinationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vaccination record not found with ID: " + id));
        vaccinationRepository.delete(record);
    }

    private VaccinationStatus computeStatus(LocalDate dueDate) {
        LocalDate today = LocalDate.now();
        if (dueDate.isBefore(today)) {
            return VaccinationStatus.OVERDUE;
        } else if (dueDate.isBefore(today.plusDays(30))) {
            return VaccinationStatus.DUE_SOON;
        } else {
            return VaccinationStatus.UP_TO_DATE;
        }
    }

    private void refreshStatusIfNeeded(VaccinationRecord record) {
        VaccinationStatus currentComputed = computeStatus(record.getDueDate());
        if (record.getStatus() != currentComputed) {
            record.setStatus(currentComputed);
        }
    }

    private VaccinationResponse mapToResponse(VaccinationRecord record) {
        return VaccinationResponse.builder()
                .id(record.getId())
                .petId(record.getPet().getId())
                .petName(record.getPet().getName())
                .petSpecies(record.getPet().getSpecies())
                .petBreed(record.getPet().getBreed())
                .veterinarianId(record.getVeterinarian().getId())
                .veterinarianName(record.getVeterinarian().getFullName())
                .vaccineName(record.getVaccineName())
                .batchNumber(record.getBatchNumber())
                .administeredDate(record.getAdministeredDate())
                .dueDate(record.getDueDate())
                .status(record.getStatus())
                .notes(record.getNotes())
                .build();
    }
}
