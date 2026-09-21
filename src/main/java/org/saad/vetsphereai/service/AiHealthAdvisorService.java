package org.saad.vetsphereai.service;

import org.saad.vetsphereai.dto.AiHealthAdvisorRequest;
import org.saad.vetsphereai.dto.AiHealthAdvisorResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AiHealthAdvisorService {

    public AiHealthAdvisorResponse generateHealthAdvice(AiHealthAdvisorRequest request) {
        String symptoms = request.getSymptoms() != null ? request.getSymptoms().toLowerCase() : "";
        String species = request.getSpecies() != null ? request.getSpecies().toLowerCase() : "pet";
        String petName = request.getPetName() != null ? request.getPetName() : "the pet";

        String triageLevel = "LOW";
        List<String> possibleConditions = new ArrayList<>();
        List<String> recommendedActions = new ArrayList<>();
        String dosageNotes = "Dosage guidance depends on precise weight and veterinary prescription.";

        // Clinical Triage evaluation engine
        if (symptoms.contains("bleed") || symptoms.contains("unconscious") || symptoms.contains("seizure") || symptoms.contains("collapse") || symptoms.contains("cannot breathe")) {
            triageLevel = "EMERGENCY";
            possibleConditions.add("Acute Trauma / Internal Hemorrhage");
            possibleConditions.add("Severe Neurological Disorder / Status Epilepticus");
            possibleConditions.add("Respiratory Arrest or Severe Obstruction");

            recommendedActions.add("IMMEDIATE EMERGENCY VETERINARY CARE REQUIRED!");
            recommendedActions.add("Keep " + petName + " warm, quiet, and clear airways.");
            recommendedActions.add("Do NOT administer any oral fluids or human medications.");
        } else if (symptoms.contains("fever") || symptoms.contains("vomit") || symptoms.contains("diarrhea") || symptoms.contains("lethargy") || symptoms.contains("limp")) {
            triageLevel = "URGENT";
            possibleConditions.add("Acute Gastroenteritis / Intestinal Infection");
            possibleConditions.add("Systemic Infection or Viral Pathogen");
            possibleConditions.add("Musculoskeletal Injury / Joint Inflammation");

            recommendedActions.add("Schedule an urgent veterinary consultation within 12-24 hours.");
            recommendedActions.add("Provide fresh water in small quantities to prevent dehydration.");
            recommendedActions.add("Isolate " + petName + " from other pets if infectious symptoms persist.");
        } else if (symptoms.contains("scratch") || symptoms.contains("cough") || symptoms.contains("sneeze") || symptoms.contains("appetite")) {
            triageLevel = "MODERATE";
            possibleConditions.add("Upper Respiratory Tract Irritation / Kennel Cough");
            possibleConditions.add("Dermatitis / Allergic Hypersensitivity");
            possibleConditions.add("Mild Dietary Indiscretion");

            recommendedActions.add("Monitor symptoms closely over the next 24-48 hours.");
            recommendedActions.add("Maintain a calm environment and check hydration levels.");
            recommendedActions.add("Contact your veterinarian if symptoms worsen or appetite drops further.");
        } else {
            triageLevel = "LOW";
            possibleConditions.add("General Wellness Check Recommended");
            possibleConditions.add("Mild Stress or Environmental Change Response");

            recommendedActions.add("Continue routine daily care, diet, and exercise.");
            recommendedActions.add("Ensure annual vaccination and antiparasitic treatments are up to date.");
        }

        if (request.getWeightKg() != null && request.getWeightKg() > 0) {
            double weight = request.getWeightKg();
            dosageNotes = String.format("Weight calculated at %.1f kg. Standard fluid maintenance requirement ~%.0f mL/day. Always verify specific pharmaceutical dosages with a licensed vet.",
                    weight, weight * 50);
        }

        String summary = String.format("AI Veterinary Assessment for %s (%s, %s): Evaluated symptoms '%s' with a triage risk status of %s.",
                petName, request.getSpecies(), request.getBreed() != null ? request.getBreed() : "Breed unknown", request.getSymptoms(), triageLevel);

        String preventiveTips = String.format("Keep %s up to date with core %s vaccines (e.g., Rabies, DHPP/FVRCP), maintain tick/flea prevention, and schedule semiannual wellness evaluations.",
                petName, species);

        return AiHealthAdvisorResponse.builder()
                .triageLevel(triageLevel)
                .summary(summary)
                .possibleConditions(possibleConditions)
                .recommendedActions(recommendedActions)
                .dosageAssistantNotes(dosageNotes)
                .preventiveCareTips(preventiveTips)
                .disclaimer("VetSphereAI Health Advisor is an AI clinical assistant tool designed to support veterinary care decision making. It does not replace direct diagnosis by a licensed veterinarian.")
                .timestamp(LocalDateTime.now())
                .build();
    }
}
