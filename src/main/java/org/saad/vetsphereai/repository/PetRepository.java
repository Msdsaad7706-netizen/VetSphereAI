package org.saad.vetsphereai.repository;

import org.saad.vetsphereai.entity.Pet;
import org.saad.vetsphereai.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PetRepository  extends JpaRepository<Pet , Long> {

    List<Pet> findByOwner(User owner);
}
