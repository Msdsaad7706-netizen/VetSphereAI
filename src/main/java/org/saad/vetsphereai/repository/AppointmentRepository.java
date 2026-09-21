package org.saad.vetsphereai.repository;

import org.saad.vetsphereai.entity.Appointment;
import org.saad.vetsphereai.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPetId(Long petId);
    List<Appointment> findByVeterinarianId(Long vetId);
    long countByStatus(AppointmentStatus status);
}
