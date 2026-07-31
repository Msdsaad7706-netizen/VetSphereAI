package org.saad.vetsphereai.repository;
import org.saad.vetsphereai.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
}
