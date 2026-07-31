package org.saad.vetsphereai.service;

import org.saad.vetsphereai.dto.VeterinarianRequest;
import org.saad.vetsphereai.dto.VeterinarianResponse;
import org.saad.vetsphereai.entity.Veterinarian;
import org.saad.vetsphereai.repository.VeterinarianRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VeterinarianService {
    private final VeterinarianRepository veterinarianRepository;

    public VeterinarianService(VeterinarianRepository veterinarianRepository) {
        this.veterinarianRepository = veterinarianRepository;
    }

    public VeterinarianResponse addVeterinarian(VeterinarianRequest request){
        Veterinarian veterinarian = new Veterinarian();
        veterinarian.setFullName(request.getFullName());
        veterinarian.setSpecialization(request.getSpecialization());
        veterinarian.setExperiance(request.getExperiance());
        veterinarian.setQualification(request.getQualification());
        veterinarian.setPhone(request.getPhone());
        veterinarian.setEmail(request.getEmail());

        Veterinarian savedVeterinarian = veterinarianRepository.save(veterinarian);
        return new VeterinarianResponse(
                savedVeterinarian.getId(),
                savedVeterinarian.getFullName(),
                savedVeterinarian.getSpecialization(),
                savedVeterinarian.getExperiance(),
                savedVeterinarian.getQualification(),
                savedVeterinarian.getPhone(),
                savedVeterinarian.getEmail()
        );
    }
    public List<VeterinarianResponse> getAllVeterinarians(){
        return veterinarianRepository.findAll()
                .stream()
                .map(vet -> new VeterinarianResponse(
                        vet.getId(),
                        vet.getFullName(),
                        vet.getSpecialization(),
                        vet.getExperiance(),
                        vet.getQualification(),
                        vet.getEmail(),
                        vet.getPhone()
                )).collect(Collectors.toList());
    }
    public VeterinarianResponse getVeterinarianById(Long id){
        Veterinarian vet = veterinarianRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("veterinarian not found"));
        return new VeterinarianResponse(vet.getId(), vet.getFullName(), vet.getSpecialization(), vet.getExperiance(), vet.getQualification(), vet.getPhone(), vet.getEmail());

    }

    public VeterinarianResponse updateVeterinarian(Long id,
                                                   VeterinarianRequest request){
        Veterinarian vet =veterinarianRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("veterinarian not found "));
        vet.setFullName(request.getFullName());
        vet.setSpecialization(request.getSpecialization());
        vet.setExperiance(request.getExperiance());
        vet.setQualification(request.getQualification());
        vet.setPhone(request.getPhone());
        vet.setEmail(request.getEmail());

        Veterinarian updated = veterinarianRepository.save(vet);
        return  new VeterinarianResponse(updated.getId(), updated.getFullName(), updated.getSpecialization(), updated.getExperiance(), updated.getQualification(), updated.getPhone(), updated.getEmail());

    }

    public void  deleteVeterinarian(Long id)
    {
        if (! veterinarianRepository.existsById(id)){
            throw new RuntimeException("veterinarian not found");
        }
        veterinarianRepository.deleteById(id);
    }
}
