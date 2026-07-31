package org.saad.vetsphereai.controller;

import org.saad.vetsphereai.dto.PetRequest;
import org.saad.vetsphereai.dto.PetResponse;
import org.saad.vetsphereai.service.PetService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }
    @PostMapping
    public PetResponse addPet(@RequestBody PetRequest request,
                              Authentication authentication){
        String email = authentication.getName();
        return petService.addPet(request, email);
    }

    @GetMapping("/my")
    public List<PetResponse>getMyPets(Authentication authentication) {
        String email =authentication.getName();
        return petService.getMyPets(email);
    }

    @GetMapping("/{id}")
    public PetResponse getPetById(@PathVariable Long id){
        return petService.getPetById(id);
    }

    @PutMapping("{id}")
    public PetResponse updatedPet(@PathVariable Long id,
                                  @RequestBody PetRequest request){
        return petService.updatePet(id, request);
    }

    @DeleteMapping("/{id}")
    public String deletePet(@PathVariable Long id){
        petService.deletePet(id);
        return "pet deleted successfully";
    }



    }

