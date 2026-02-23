package com.shopwise.repository;

import com.shopwise.entity.Appointment;
import com.shopwise.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByClientId(Long clientId);

    List<Appointment> findByStatus(AppointmentStatus status);

    List<Appointment> findByAppointmentDate(LocalDate date);

    List<Appointment> findByAppointmentDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT a FROM Appointment a WHERE " +
           "(:clientId IS NULL OR a.client.id = :clientId) AND " +
           "(:status IS NULL OR a.status = :status) AND " +
           "(:date IS NULL OR a.appointmentDate = :date)")
    List<Appointment> findByFilters(
            @Param("clientId") Long clientId,
            @Param("status") AppointmentStatus status,
            @Param("date") LocalDate date);

    @Query("SELECT a FROM Appointment a WHERE a.client.id = :clientId ORDER BY a.appointmentDate DESC, a.appointmentTime DESC")
    List<Appointment> findByClientIdOrderByDateDesc(@Param("clientId") Long clientId);
}
