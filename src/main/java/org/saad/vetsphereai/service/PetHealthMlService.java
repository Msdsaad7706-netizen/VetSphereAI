package org.saad.vetsphereai.service;

import org.saad.vetsphereai.dto.PetRiskAssessmentResponse;
import org.saad.vetsphereai.entity.MedicalRecord;
import org.saad.vetsphereai.entity.Pet;
import org.saad.vetsphereai.entity.VaccinationRecord;
import org.saad.vetsphereai.entity.VaccinationStatus;
import org.saad.vetsphereai.exception.ResourceNotFoundException;
import org.saad.vetsphereai.repository.MedicalRecordRepository;
import org.saad.vetsphereai.repository.PetRepository;
import org.saad.vetsphereai.repository.VaccinationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PetHealthMlService {

    private final PetRepository petRepository;
    private final VaccinationRepository vaccinationRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    public PetHealthMlService(PetRepository petRepository,
                              VaccinationRepository vaccinationRepository,
                              MedicalRecordRepository medicalRecordRepository) {
        this.petRepository = petRepository;
        this.vaccinationRepository = vaccinationRepository;
        this.medicalRecordRepository = medicalRecordRepository;
    }

    @Transactional(readOnly = true)
    public PetRiskAssessmentResponse calculatePetHealthRisk(Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with ID: " + petId));

        int riskScore = 15; // Base baseline risk score
        List<String> riskFactors = new ArrayList<>();
        List<String> interventions = new ArrayList<>();

        // Feature 1: Age & Species Life Expectancy Factor
        int age = pet.getAge() != null ? pet.getAge() : 3;
        String species = pet.getSpecies() != null ? pet.getSpecies().toLowerCase() : "dog";

        if (species.contains("dog") || species.contains("canine")) {
            if (age >= 10) {
                riskScore += 25;
                riskFactors.add("Senior Canine Age (>10 years)");
            } else if (age >= 7) {
                riskScore += 15;
                riskFactors.add("Mature Canine Age (7-9 years)");
            }
        } else if (species.contains("cat") || species.contains("feline")) {
            if (age >= 12) {
                riskScore += 25;
                riskFactors.add("Senior Feline Age (>12 years)");
            } else if (age >= 9) {
                riskScore += 15;
                riskFactors.add("Mature Feline Age (9-11 years)");
            }
        } else if (age >= 5) {
            riskScore += 15;
            riskFactors.add("Advanced Age for Species (" + age + " years)");
        }

        // Feature 2: Vaccination Adherence & Overdue Status
        List<VaccinationRecord> vaccinations = vaccinationRepository.findByPetId(petId);
        long overdueCount = vaccinations.stream().filter(v -> v.getStatus() == VaccinationStatus.OVERDUE).count();
        long dueSoonCount = vaccinations.stream().filter(v -> v.getStatus() == VaccinationStatus.DUE_SOON).count();

        if (overdueCount > 0) {
            riskScore += (int) (overdueCount * 20);
            riskFactors.add(overdueCount + " Overdue Vaccine Booster(s)");
            interventions.add("Administer overdue vaccine boosters immediately to prevent infectious disease exposure.");
        } else if (dueSoonCount > 0) {
            riskScore += 10;
            riskFactors.add(dueSoonCount + " Vaccine(s) Due Within 30 Days");
            interventions.add("Schedule upcoming vaccination appointments before expiration.");
        }

        if (vaccinations.isEmpty()) {
            riskScore += 20;
            riskFactors.add("No Recorded Vaccination History in Clinic Database");
            interventions.add("Perform baseline immunization series.");
        }

        // Feature 3: Medical Record History & Chronic Disease Frequency
        List<MedicalRecord> medicalRecords = medicalRecordRepository.findAll().stream()
                .filter(m -> m.getPet() != null && m.getPet().getId().equals(petId))
                .toList();

        if (medicalRecords.size() >= 5) {
            riskScore += 25;
            riskFactors.add("High Medical Visit Frequency (" + medicalRecords.size() + " total clinical consultations)");
            interventions.add("Conduct comprehensive blood panel and organ function profile.");
        } else if (medicalRecords.size() >= 3) {
            riskScore += 15;
            riskFactors.add("Moderate Medical History (" + medicalRecords.size() + " clinical consultations)");
        }

        // Bound risk score between 0 and 100
        riskScore = Math.min(100, Math.max(0, riskScore));

        // Determine Risk Level classification
        String riskLevel;
        int checkupDays;

        if (riskScore >= 75) {
            riskLevel = "CRITICAL";
            checkupDays = 7;
            interventions.add("URGENT: Comprehensive clinical evaluation recommended within 7 days.");
        } else if (riskScore >= 50) {
            riskLevel = "HIGH";
            checkupDays = 14;
            interventions.add("Schedule a full veterinary health exam within 14 days.");
        } else if (riskScore >= 30) {
            riskLevel = "MEDIUM";
            checkupDays = 30;
            interventions.add("Regular checkup recommended within 30 days.");
        } else {
            riskLevel = "LOW";
            checkupDays = 90;
            interventions.add("Maintain regular annual wellness exam and balanced diet.");
        }

        if (riskFactors.isEmpty()) {
            riskFactors.add("Optimal Health Metrics & Complete Immunization Status");
        }

        return PetRiskAssessmentResponse.builder()
                .petId(pet.getId())
                .petName(pet.getName())
                .species(pet.getSpecies())
                .breed(pet.getBreed())
                .age(pet.getAge())
                .riskScore(riskScore)
                .riskLevel(riskLevel)
                .primaryRiskFactors(riskFactors)
                .recommendedInterventions(interventions)
                .nextRecommendedCheckupDays(checkupDays)
                .assessedAt(LocalDateTime.now())
                .build();
    }
}
