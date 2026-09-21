package org.saad.vetsphereai.repository;

import org.saad.vetsphereai.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findByPetId(Long petId);
    List<Prescription> findByVeterinarianId(Long vetId);
    long countByStatus(String status);
}