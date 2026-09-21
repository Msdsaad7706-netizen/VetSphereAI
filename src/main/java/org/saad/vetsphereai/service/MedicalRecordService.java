package org.saad.vetsphereai.service;

import org.saad.vetsphereai.dto.MedicalRecordRequest;
import org.saad.vetsphereai.dto.MedicalRecordResponse;
import org.saad.vetsphereai.entity.MedicalRecord;
import org.saad.vetsphereai.entity.Pet;
import org.saad.vetsphereai.entity.Veterinarian;
import org.saad.vetsphereai.repository.MedicalRecordRepository;
import org.saad.vetsphereai.repository.PetRepository;
import org.saad.vetsphereai.repository.VeterinarianRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PetRepository petRepository;
    private final VeterinarianRepository veterinarianRepository;

    public MedicalRecordService(
            MedicalRecordRepository medicalRecordRepository,
            PetRepository petRepository,
            VeterinarianRepository veterinarianRepository) {

        this.medicalRecordRepository = medicalRecordRepository;
        this.petRepository = petRepository;
        this.veterinarianRepository = veterinarianRepository;
    }

    public MedicalRecordResponse createMedicalRecord(
            MedicalRecordRequest request) {

        Pet pet = petRepository.findById(request.getPetId())
                .orElseThrow(() -> new RuntimeException("Pet not found"));

        Veterinarian veterinarian = veterinarianRepository
                .findById(request.getVeterinarianId())
                .orElseThrow(() -> new RuntimeException("Veterinarian not found"));

        MedicalRecord medicalRecord = new MedicalRecord();

        medicalRecord.setPet(pet);
        medicalRecord.setVeterinarian(veterinarian);
        medicalRecord.setDiagnosis(request.getDiagnosis());
        medicalRecord.setTreatment(request.getTreatment());
        medicalRecord.setNotes(request.getNotes());
        medicalRecord.setRecordDate(request.getRecordDate());

        MedicalRecord savedRecord =
                medicalRecordRepository.save(medicalRecord);

        return MedicalRecordResponse.builder()
                .id(savedRecord.getId())
                .petId(savedRecord.getPet().getId())
                .petName(savedRecord.getPet().getName())
                .veterinarianId(savedRecord.getVeterinarian().getId())
                .veterinarianName(
                        savedRecord.getVeterinarian().getFullName())
                .diagnosis(savedRecord.getDiagnosis())
                .treatment(savedRecord.getTreatment())
                .notes(savedRecord.getNotes())
                .recordDate(savedRecord.getRecordDate())
                .build();
    }

    public List<MedicalRecordResponse> getAllMedicalRecords(){
        return medicalRecordRepository.findAll()
                .stream()
                .map(record -> MedicalRecordResponse.builder()
                        .id(record.getId())
                        .petId(record.getPet().getId())
                        .petName(record.getPet().getName())
                        .veterinarianId(record.getVeterinarian().getId())
                        .veterinarianName(record.getVeterinarian().getFullName())
                        .diagnosis(record.getDiagnosis())
                        .treatment(record.getTreatment())
                        .notes(record.getNotes())
                        .recordDate(record.getRecordDate())
                        .build())
                .toList();
    }
    public MedicalRecordResponse getMedicalRecordById(Long id){
        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Medical record not found"));
        return
                MedicalRecordResponse.builder()
                        .id(record.getId())
                        .petId(record.getPet().getId())
                        .petName(record.getPet().getName())
                        .veterinarianId(record.getId())
                        .veterinarianName(record.getVeterinarian().getFullName())
                        .diagnosis(record.getDiagnosis())
                        .treatment(record.getTreatment())
                        .notes(record.getNotes())
                        .recordDate(record.getRecordDate())
                        .build();
    }

    public MedicalRecordResponse updateMedicalRecord(Long id, MedicalRecordRequest request) {
        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medical record not found"));
        Pet pet = petRepository.findById(request.getPetId())
                .orElseThrow(() -> new RuntimeException("Pet not found"));
        Veterinarian veterinarian = veterinarianRepository.findById(request.getVeterinarianId())
                .orElseThrow(() -> new RuntimeException("Veterianarian not found"));
        record.setPet(pet);
        record.setVeterinarian(veterinarian);
        record.setDiagnosis(request.getDiagnosis());
        record.setTreatment(request.getTreatment());
        record.setNotes(request.getNotes());
        record.setRecordDate(request.getRecordDate());
        MedicalRecord updatedRecord = medicalRecordRepository.save(record);

        return
                MedicalRecordResponse.builder()
                        .id(updatedRecord.getId())
                        .petId(updatedRecord.getPet().getId())
                        .petName(updatedRecord.getPet().getName())
                        .veterinarianId(updatedRecord.getVeterinarian().getId())
                        .veterinarianName(updatedRecord.getVeterinarian().getFullName())
                        .diagnosis(updatedRecord.getDiagnosis())
                        .treatment(updatedRecord.getTreatment())
                        .notes(updatedRecord.getNotes())
                        .recordDate(updatedRecord.getRecordDate())
                        .build();
                         }
           public void deleteMedicalRecord(Long id){
               MedicalRecord record =medicalRecordRepository.findById(id)
                       .orElseThrow(()-> new RuntimeException("Medical record new found"));
               medicalRecordRepository.delete(record);
    }
}