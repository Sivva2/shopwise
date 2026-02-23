package com.shopwise.dto;

import com.shopwise.entity.AppointmentStatus;
import com.shopwise.entity.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class DtoTest {

    // ========== CLIENT DTO TESTS ==========

    @Test
    @DisplayName("ClientDTO - Builder et Getters")
    void clientDTO_BuilderAndGetters() {
        LocalDateTime now = LocalDateTime.now();
        
        ClientDTO dto = ClientDTO.builder()
                .id(1L)
                .firstName("Marie")
                .lastName("Dupont")
                .email("marie@email.com")
                .phone("0612345678")
                .loyaltyPoints(100)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getFirstName()).isEqualTo("Marie");
        assertThat(dto.getLastName()).isEqualTo("Dupont");
        assertThat(dto.getEmail()).isEqualTo("marie@email.com");
        assertThat(dto.getPhone()).isEqualTo("0612345678");
        assertThat(dto.getLoyaltyPoints()).isEqualTo(100);
        assertThat(dto.getCreatedAt()).isEqualTo(now);
        assertThat(dto.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("ClientDTO - Setters")
    void clientDTO_Setters() {
        ClientDTO dto = new ClientDTO();
        LocalDateTime now = LocalDateTime.now();
        
        dto.setId(1L);
        dto.setFirstName("Jean");
        dto.setLastName("Martin");
        dto.setEmail("jean@email.com");
        dto.setPhone("0698765432");
        dto.setLoyaltyPoints(50);
        dto.setCreatedAt(now);
        dto.setUpdatedAt(now);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getFirstName()).isEqualTo("Jean");
        assertThat(dto.getLastName()).isEqualTo("Martin");
        assertThat(dto.getEmail()).isEqualTo("jean@email.com");
        assertThat(dto.getPhone()).isEqualTo("0698765432");
        assertThat(dto.getLoyaltyPoints()).isEqualTo(50);
    }

    @Test
    @DisplayName("ClientDTO - getFullName")
    void clientDTO_GetFullName() {
        ClientDTO dto = ClientDTO.builder()
                .firstName("Marie")
                .lastName("Dupont")
                .build();

        assertThat(dto.getFullName()).isEqualTo("Marie Dupont");
    }

    @Test
    @DisplayName("ClientDTO - NoArgsConstructor")
    void clientDTO_NoArgsConstructor() {
        ClientDTO dto = new ClientDTO();
        assertThat(dto).isNotNull();
    }

    @Test
    @DisplayName("ClientDTO - AllArgsConstructor")
    void clientDTO_AllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        ClientDTO dto = new ClientDTO(1L, "Marie", "Dupont", "marie@email.com", 
                "0612345678", 100, now, now);
        
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getFirstName()).isEqualTo("Marie");
    }

    @Test
    @DisplayName("ClientDTO - equals et hashCode")
    void clientDTO_EqualsAndHashCode() {
        ClientDTO dto1 = ClientDTO.builder().id(1L).email("test@email.com").build();
        ClientDTO dto2 = ClientDTO.builder().id(1L).email("test@email.com").build();
        ClientDTO dto3 = ClientDTO.builder().id(2L).email("other@email.com").build();

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1).isNotEqualTo(dto3);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    @DisplayName("ClientDTO - toString")
    void clientDTO_ToString() {
        ClientDTO dto = ClientDTO.builder()
                .id(1L)
                .firstName("Marie")
                .lastName("Dupont")
                .build();

        String toString = dto.toString();
        assertThat(toString).contains("Marie");
        assertThat(toString).contains("Dupont");
    }

    // ========== SERVICE DTO TESTS ==========

    @Test
    @DisplayName("ServiceDTO - Builder et Getters")
    void serviceDTO_BuilderAndGetters() {
        LocalDateTime now = LocalDateTime.now();
        
        ServiceDTO dto = ServiceDTO.builder()
                .id(1L)
                .name("Consultation")
                .description("Description")
                .durationMinutes(30)
                .pointsAwarded(10)
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Consultation");
        assertThat(dto.getDescription()).isEqualTo("Description");
        assertThat(dto.getDurationMinutes()).isEqualTo(30);
        assertThat(dto.getPointsAwarded()).isEqualTo(10);
        assertThat(dto.getActive()).isTrue();
    }

    @Test
    @DisplayName("ServiceDTO - Setters")
    void serviceDTO_Setters() {
        ServiceDTO dto = new ServiceDTO();
        
        dto.setId(1L);
        dto.setName("Premium");
        dto.setDescription("Desc");
        dto.setDurationMinutes(60);
        dto.setPointsAwarded(25);
        dto.setActive(false);

        assertThat(dto.getName()).isEqualTo("Premium");
        assertThat(dto.getDurationMinutes()).isEqualTo(60);
        assertThat(dto.getActive()).isFalse();
    }

    @Test
    @DisplayName("ServiceDTO - NoArgsConstructor")
    void serviceDTO_NoArgsConstructor() {
        ServiceDTO dto = new ServiceDTO();
        assertThat(dto).isNotNull();
    }

    @Test
    @DisplayName("ServiceDTO - AllArgsConstructor")
    void serviceDTO_AllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        ServiceDTO dto = new ServiceDTO(1L, "Consultation", "Desc", 30, 10, true, now, now);
        
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Consultation");
    }

    // ========== APPOINTMENT DTO TESTS ==========

    @Test
    @DisplayName("AppointmentDTO - Builder et Getters")
    void appointmentDTO_BuilderAndGetters() {
        LocalDateTime now = LocalDateTime.now();
        
        AppointmentDTO dto = AppointmentDTO.builder()
                .id(1L)
                .clientId(1L)
                .clientName("Marie Dupont")
                .clientEmail("marie@email.com")
                .serviceId(1L)
                .serviceName("Consultation")
                .serviceDuration(30)
                .servicePoints(10)
                .appointmentDate(LocalDate.of(2025, 3, 1))
                .appointmentTime(LocalTime.of(10, 0))
                .status(AppointmentStatus.SCHEDULED)
                .notes("Note")
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getClientId()).isEqualTo(1L);
        assertThat(dto.getClientName()).isEqualTo("Marie Dupont");
        assertThat(dto.getClientEmail()).isEqualTo("marie@email.com");
        assertThat(dto.getServiceId()).isEqualTo(1L);
        assertThat(dto.getServiceName()).isEqualTo("Consultation");
        assertThat(dto.getServiceDuration()).isEqualTo(30);
        assertThat(dto.getServicePoints()).isEqualTo(10);
        assertThat(dto.getAppointmentDate()).isEqualTo(LocalDate.of(2025, 3, 1));
        assertThat(dto.getAppointmentTime()).isEqualTo(LocalTime.of(10, 0));
        assertThat(dto.getStatus()).isEqualTo(AppointmentStatus.SCHEDULED);
        assertThat(dto.getNotes()).isEqualTo("Note");
    }

    @Test
    @DisplayName("AppointmentDTO - Setters")
    void appointmentDTO_Setters() {
        AppointmentDTO dto = new AppointmentDTO();
        
        dto.setId(1L);
        dto.setClientId(2L);
        dto.setClientName("Jean Martin");
        dto.setServiceId(3L);
        dto.setServiceName("Premium");
        dto.setAppointmentDate(LocalDate.of(2025, 4, 15));
        dto.setAppointmentTime(LocalTime.of(14, 30));
        dto.setStatus(AppointmentStatus.COMPLETED);
        dto.setNotes("Updated note");

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getClientId()).isEqualTo(2L);
        assertThat(dto.getStatus()).isEqualTo(AppointmentStatus.COMPLETED);
    }

    @Test
    @DisplayName("AppointmentDTO - NoArgsConstructor")
    void appointmentDTO_NoArgsConstructor() {
        AppointmentDTO dto = new AppointmentDTO();
        assertThat(dto).isNotNull();
    }

    // ========== POINT TRANSACTION DTO TESTS ==========

    @Test
    @DisplayName("PointTransactionDTO - Builder et Getters")
    void pointTransactionDTO_BuilderAndGetters() {
        LocalDateTime now = LocalDateTime.now();
        
        PointTransactionDTO dto = PointTransactionDTO.builder()
                .id(1L)
                .clientId(1L)
                .clientName("Marie Dupont")
                .appointmentId(1L)
                .points(10)
                .transactionType(TransactionType.EARNED)
                .description("Points gagnés")
                .createdAt(now)
                .build();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getClientId()).isEqualTo(1L);
        assertThat(dto.getClientName()).isEqualTo("Marie Dupont");
        assertThat(dto.getAppointmentId()).isEqualTo(1L);
        assertThat(dto.getPoints()).isEqualTo(10);
        assertThat(dto.getTransactionType()).isEqualTo(TransactionType.EARNED);
        assertThat(dto.getDescription()).isEqualTo("Points gagnés");
    }

    @Test
    @DisplayName("PointTransactionDTO - Setters")
    void pointTransactionDTO_Setters() {
        PointTransactionDTO dto = new PointTransactionDTO();
        
        dto.setId(1L);
        dto.setClientId(2L);
        dto.setClientName("Jean Martin");
        dto.setAppointmentId(null);
        dto.setPoints(-50);
        dto.setTransactionType(TransactionType.REDEEMED);
        dto.setDescription("Utilisé");

        assertThat(dto.getPoints()).isEqualTo(-50);
        assertThat(dto.getTransactionType()).isEqualTo(TransactionType.REDEEMED);
        assertThat(dto.getAppointmentId()).isNull();
    }

    @Test
    @DisplayName("PointTransactionDTO - NoArgsConstructor")
    void pointTransactionDTO_NoArgsConstructor() {
        PointTransactionDTO dto = new PointTransactionDTO();
        assertThat(dto).isNotNull();
    }

    // ========== UPDATE STATUS DTO TESTS ==========

    @Test
    @DisplayName("UpdateStatusDTO - Constructor et Getter")
    void updateStatusDTO_ConstructorAndGetter() {
        UpdateStatusDTO dto = new UpdateStatusDTO(AppointmentStatus.COMPLETED);
        
        assertThat(dto.getStatus()).isEqualTo(AppointmentStatus.COMPLETED);
    }

    @Test
    @DisplayName("UpdateStatusDTO - Setter")
    void updateStatusDTO_Setter() {
        UpdateStatusDTO dto = new UpdateStatusDTO();
        dto.setStatus(AppointmentStatus.CANCELLED);
        
        assertThat(dto.getStatus()).isEqualTo(AppointmentStatus.CANCELLED);
    }

    @Test
    @DisplayName("UpdateStatusDTO - NoArgsConstructor")
    void updateStatusDTO_NoArgsConstructor() {
        UpdateStatusDTO dto = new UpdateStatusDTO();
        assertThat(dto).isNotNull();
    }

    @Test
    @DisplayName("UpdateStatusDTO - AllArgsConstructor")
    void updateStatusDTO_AllArgsConstructor() {
        UpdateStatusDTO dto = new UpdateStatusDTO(AppointmentStatus.SCHEDULED);
        assertThat(dto.getStatus()).isEqualTo(AppointmentStatus.SCHEDULED);
    }
}
