package com.shopwise.service;

import com.shopwise.dto.AppointmentDTO;
import com.shopwise.entity.*;
import com.shopwise.exception.BusinessException;
import com.shopwise.exception.ResourceNotFoundException;
import com.shopwise.repository.AppointmentRepository;
import com.shopwise.repository.ClientRepository;
import com.shopwise.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ClientRepository clientRepository;
    private final ServiceRepository serviceRepository;
    private final PointTransactionService pointTransactionService;

    public List<AppointmentDTO> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public AppointmentDTO getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rendez-vous", id));
        return toDTO(appointment);
    }

    public List<AppointmentDTO> getAppointmentsByClient(Long clientId) {
        return appointmentRepository.findByClientIdOrderByDateDesc(clientId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<AppointmentDTO> getAppointmentsByFilters(Long clientId, AppointmentStatus status, LocalDate date) {
        return appointmentRepository.findByFilters(clientId, status, date).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public AppointmentDTO createAppointment(AppointmentDTO appointmentDTO) {
        Client client = clientRepository.findById(appointmentDTO.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client", appointmentDTO.getClientId()));

        Service service = serviceRepository.findById(appointmentDTO.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service", appointmentDTO.getServiceId()));

        if (!service.getActive()) {
            throw new BusinessException("Ce service n'est plus disponible");
        }

        Appointment appointment = Appointment.builder()
                .client(client)
                .service(service)
                .appointmentDate(appointmentDTO.getAppointmentDate())
                .appointmentTime(appointmentDTO.getAppointmentTime())
                .status(AppointmentStatus.SCHEDULED)
                .notes(appointmentDTO.getNotes())
                .build();

        appointment = appointmentRepository.save(appointment);
        return toDTO(appointment);
    }

    public AppointmentDTO updateAppointment(Long id, AppointmentDTO appointmentDTO) {
        Appointment existingAppointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rendez-vous", id));

        if (existingAppointment.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new BusinessException("Impossible de modifier un rendez-vous déjà honoré ou annulé");
        }

        if (appointmentDTO.getClientId() != null && 
                !appointmentDTO.getClientId().equals(existingAppointment.getClient().getId())) {
            Client client = clientRepository.findById(appointmentDTO.getClientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Client", appointmentDTO.getClientId()));
            existingAppointment.setClient(client);
        }

        if (appointmentDTO.getServiceId() != null && 
                !appointmentDTO.getServiceId().equals(existingAppointment.getService().getId())) {
            Service service = serviceRepository.findById(appointmentDTO.getServiceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Service", appointmentDTO.getServiceId()));
            existingAppointment.setService(service);
        }

        if (appointmentDTO.getAppointmentDate() != null) {
            existingAppointment.setAppointmentDate(appointmentDTO.getAppointmentDate());
        }
        if (appointmentDTO.getAppointmentTime() != null) {
            existingAppointment.setAppointmentTime(appointmentDTO.getAppointmentTime());
        }
        if (appointmentDTO.getNotes() != null) {
            existingAppointment.setNotes(appointmentDTO.getNotes());
        }

        existingAppointment = appointmentRepository.save(existingAppointment);
        return toDTO(existingAppointment);
    }

    public AppointmentDTO updateStatus(Long id, AppointmentStatus newStatus) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rendez-vous", id));

        AppointmentStatus currentStatus = appointment.getStatus();

        // Validation des transitions de statut
        if (currentStatus == AppointmentStatus.COMPLETED) {
            throw new BusinessException("Un rendez-vous honoré ne peut plus être modifié");
        }

        if (currentStatus == AppointmentStatus.CANCELLED && newStatus != AppointmentStatus.SCHEDULED) {
            throw new BusinessException("Un rendez-vous annulé ne peut être que replanifié");
        }

        appointment.setStatus(newStatus);
        appointment = appointmentRepository.save(appointment);

        // Attribution automatique des points si le rendez-vous est honoré
        if (newStatus == AppointmentStatus.COMPLETED) {
            pointTransactionService.awardPointsForAppointment(appointment);
        }

        return toDTO(appointment);
    }

    public void deleteAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rendez-vous", id));

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BusinessException("Impossible de supprimer un rendez-vous honoré");
        }

        appointmentRepository.deleteById(id);
    }

    // Mapper Entity -> DTO
    public AppointmentDTO toDTO(Appointment appointment) {
        return AppointmentDTO.builder()
                .id(appointment.getId())
                .clientId(appointment.getClient().getId())
                .clientName(appointment.getClient().getFullName())
                .clientEmail(appointment.getClient().getEmail())
                .serviceId(appointment.getService().getId())
                .serviceName(appointment.getService().getName())
                .serviceDuration(appointment.getService().getDurationMinutes())
                .servicePoints(appointment.getService().getPointsAwarded())
                .appointmentDate(appointment.getAppointmentDate())
                .appointmentTime(appointment.getAppointmentTime())
                .status(appointment.getStatus())
                .notes(appointment.getNotes())
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .build();
    }
}
