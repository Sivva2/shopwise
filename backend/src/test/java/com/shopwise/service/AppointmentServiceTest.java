package com.shopwise.service;

import com.shopwise.dto.AppointmentDTO;
import com.shopwise.entity.*;
import com.shopwise.exception.BusinessException;
import com.shopwise.exception.ResourceNotFoundException;
import com.shopwise.repository.AppointmentRepository;
import com.shopwise.repository.ClientRepository;
import com.shopwise.repository.ServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private PointTransactionService pointTransactionService;

    @InjectMocks
    private AppointmentService appointmentService;

    private Client client;
    private Service service;
    private Appointment appointment;
    private AppointmentDTO appointmentDTO;

    @BeforeEach
    void setUp() {
        client = Client.builder()
                .id(1L)
                .firstName("Marie")
                .lastName("Dupont")
                .email("marie@email.com")
                .loyaltyPoints(100)
                .build();

        service = Service.builder()
                .id(1L)
                .name("Consultation")
                .durationMinutes(30)
                .pointsAwarded(10)
                .active(true)
                .build();

        appointment = Appointment.builder()
                .id(1L)
                .client(client)
                .service(service)
                .appointmentDate(LocalDate.now().plusDays(1))
                .appointmentTime(LocalTime.of(10, 0))
                .status(AppointmentStatus.SCHEDULED)
                .build();

        appointmentDTO = AppointmentDTO.builder()
                .id(1L)
                .clientId(1L)
                .serviceId(1L)
                .appointmentDate(LocalDate.now().plusDays(1))
                .appointmentTime(LocalTime.of(10, 0))
                .status(AppointmentStatus.SCHEDULED)
                .build();
    }

    @Test
    @DisplayName("getAllAppointments - Retourne tous les RDV")
    void getAllAppointments_ReturnsAll() {
        when(appointmentRepository.findAll()).thenReturn(Arrays.asList(appointment));

        List<AppointmentDTO> result = appointmentService.getAllAppointments();

        assertThat(result).hasSize(1);
        verify(appointmentRepository).findAll();
    }

    @Test
    @DisplayName("getAppointmentById - Retourne le RDV quand il existe")
    void getAppointmentById_WhenExists_ReturnsAppointment() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        AppointmentDTO result = appointmentService.getAppointmentById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getClientName()).isEqualTo("Marie Dupont");
    }

    @Test
    @DisplayName("getAppointmentById - Lance exception quand RDV n'existe pas")
    void getAppointmentById_WhenNotFound_ThrowsException() {
        when(appointmentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.getAppointmentById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("getAppointmentsByClient - Retourne les RDV du client")
    void getAppointmentsByClient_ReturnsClientAppointments() {
        when(appointmentRepository.findByClientIdOrderByDateDesc(1L)).thenReturn(Arrays.asList(appointment));

        List<AppointmentDTO> result = appointmentService.getAppointmentsByClient(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("getAppointmentsByFilters - Filtre correctement")
    void getAppointmentsByFilters_FiltersCorrectly() {
        when(appointmentRepository.findByFilters(1L, AppointmentStatus.SCHEDULED, null))
                .thenReturn(Arrays.asList(appointment));

        List<AppointmentDTO> result = appointmentService.getAppointmentsByFilters(1L, AppointmentStatus.SCHEDULED, null);

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("createAppointment - Crée un nouveau RDV")
    void createAppointment_WithValidData_ReturnsCreatedAppointment() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(service));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        AppointmentDTO result = appointmentService.createAppointment(appointmentDTO);

        assertThat(result).isNotNull();
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    @DisplayName("createAppointment - Lance exception si client n'existe pas")
    void createAppointment_WhenClientNotFound_ThrowsException() {
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.createAppointment(appointmentDTO))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("createAppointment - Lance exception si service n'existe pas")
    void createAppointment_WhenServiceNotFound_ThrowsException() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(serviceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.createAppointment(appointmentDTO))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("createAppointment - Lance exception si service inactif")
    void createAppointment_WhenServiceInactive_ThrowsException() {
        service.setActive(false);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(service));

        assertThatThrownBy(() -> appointmentService.createAppointment(appointmentDTO))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("updateAppointment - Met à jour le RDV planifié")
    void updateAppointment_WhenScheduled_UpdatesAppointment() {
        AppointmentDTO updateDTO = AppointmentDTO.builder()
                .appointmentDate(LocalDate.now().plusDays(2))
                .appointmentTime(LocalTime.of(14, 0))
                .notes("Nouvelle note")
                .build();

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        AppointmentDTO result = appointmentService.updateAppointment(1L, updateDTO);

        assertThat(result).isNotNull();
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    @DisplayName("updateAppointment - Lance exception si RDV déjà honoré")
    void updateAppointment_WhenCompleted_ThrowsException() {
        appointment.setStatus(AppointmentStatus.COMPLETED);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> appointmentService.updateAppointment(1L, appointmentDTO))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("updateStatus - Met à jour le statut vers COMPLETED et attribue les points")
    void updateStatus_ToCompleted_AwardsPoints() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
        doNothing().when(pointTransactionService).awardPointsForAppointment(any(Appointment.class));

        AppointmentDTO result = appointmentService.updateStatus(1L, AppointmentStatus.COMPLETED);

        assertThat(result).isNotNull();
        verify(pointTransactionService).awardPointsForAppointment(any(Appointment.class));
    }

    @Test
    @DisplayName("updateStatus - Met à jour le statut vers CANCELLED")
    void updateStatus_ToCancelled_UpdatesStatus() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        AppointmentDTO result = appointmentService.updateStatus(1L, AppointmentStatus.CANCELLED);

        assertThat(result).isNotNull();
        verify(pointTransactionService, never()).awardPointsForAppointment(any());
    }

    @Test
    @DisplayName("updateStatus - Lance exception si RDV déjà COMPLETED")
    void updateStatus_WhenAlreadyCompleted_ThrowsException() {
        appointment.setStatus(AppointmentStatus.COMPLETED);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> appointmentService.updateStatus(1L, AppointmentStatus.CANCELLED))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("deleteAppointment - Supprime le RDV planifié")
    void deleteAppointment_WhenScheduled_DeletesAppointment() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        doNothing().when(appointmentRepository).deleteById(1L);

        appointmentService.deleteAppointment(1L);

        verify(appointmentRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteAppointment - Lance exception si RDV honoré")
    void deleteAppointment_WhenCompleted_ThrowsException() {
        appointment.setStatus(AppointmentStatus.COMPLETED);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> appointmentService.deleteAppointment(1L))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("toDTO - Convertit correctement")
    void toDTO_ConvertsCorrectly() {
        AppointmentDTO result = appointmentService.toDTO(appointment);

        assertThat(result.getId()).isEqualTo(appointment.getId());
        assertThat(result.getClientId()).isEqualTo(client.getId());
        assertThat(result.getClientName()).isEqualTo("Marie Dupont");
        assertThat(result.getServiceId()).isEqualTo(service.getId());
        assertThat(result.getServiceName()).isEqualTo("Consultation");
    }
}
