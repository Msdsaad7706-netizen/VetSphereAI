package org.saad.vetsphereai.repository;

import org.saad.vetsphereai.entity.Veterinarian;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VeterinarianRepository extends JpaRepository<Veterinarian,Long> {
}
