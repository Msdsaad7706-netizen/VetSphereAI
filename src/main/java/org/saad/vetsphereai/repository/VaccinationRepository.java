package org.saad.vetsphereai.repository;

import org.saad.vetsphereai.entity.VaccinationRecord;
import org.saad.vetsphereai.entity.VaccinationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VaccinationRepository extends JpaRepository<VaccinationRecord, Long> {
    List<VaccinationRecord> findByPetId(Long petId);
    List<VaccinationRecord> findByStatus(VaccinationStatus status);
    List<VaccinationRecord> findByDueDateBefore(LocalDate date);
    long countByStatus(VaccinationStatus status);
}
