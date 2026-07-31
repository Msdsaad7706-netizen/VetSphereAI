package org.saad.vetsphereai.service;

import org.saad.vetsphereai.dto.PetRequest;
import org.saad.vetsphereai.dto.PetResponse;
import org.saad.vetsphereai.entity.Pet;
import org.saad.vetsphereai.entity.User;
import org.saad.vetsphereai.repository.PetRepository;
import org.saad.vetsphereai.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PetService {
    private final PetRepository petRepository;
    private final UserRepository userRepository;


    public PetService(PetRepository petRepository, UserRepository userRepository) {
        this.petRepository = petRepository;
        this.userRepository = userRepository;
    }

    public PetResponse addPet(PetRequest request, String email) {
        User owner = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Owner nor found"));
        Pet pet = new Pet();
        pet.setName(request.getName());
        pet.setSpecies(request.getSpecies());
        pet.setBreed(request.getBreed());
        pet.setAge(request.getAge());
        pet.setGender(request.getGender());
        pet.setOwner(owner);
        Pet savedPet = petRepository.save(pet);
        return new PetResponse(
                savedPet.getId(),
                savedPet.getName(),
                savedPet.getSpecies(),
                savedPet.getBreed(),
                savedPet.getAge(),
                savedPet.getGender(),
                owner.getFullname()
        );
    }

        public List<PetResponse> getMyPets(String email){
            User owner = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Owners not found"));
            List<Pet> pets = petRepository.findByOwner(owner);
            return pets.stream()
                    .map(pet-> new PetResponse(
                            pet.getId(),
                            pet.getName(),
                            pet.getSpecies(),
                            pet.getBreed(),
                            pet.getAge(),
                            pet.getGender(),
                            owner.getFullname()
                    )).collect(Collectors.toList());
        }

        public PetResponse getPetById(Long id){
        Pet pet =petRepository.findById(id).orElseThrow(()-> new RuntimeException("Pet not found"));
        return new PetResponse(
                pet.getId(),
                pet.getName(),
                pet.getSpecies(),
                pet.getBreed(),
                pet.getAge(),
                pet.getGender(),
                pet.getOwner().getFullname()
        );
        }
        public PetResponse updatePet(Long id, PetRequest request){
        Pet pet = petRepository.findById(id).orElseThrow(()-> new RuntimeException("Pet nit found"));
        pet.setName(request.getName());
        pet.setSpecies(request.getSpecies());
        pet.setBreed(request.getBreed());
        pet.setAge(request.getAge());
        pet.setGender(request.getGender());
        Pet updatedPet = petRepository.save(pet);
        return new PetResponse(
                updatedPet.getId(),
                updatedPet.getName(),
                updatedPet.getSpecies(),
                updatedPet.getBreed(),
                updatedPet.getAge(),
                updatedPet.getGender(),
                updatedPet.getOwner().getFullname()
        );

        }

        public  void deletePet(Long id){
        if (! petRepository.existsById(id)){
            throw new RuntimeException("pet not found");
        }
        petRepository.deleteById(id);
        }
    }

