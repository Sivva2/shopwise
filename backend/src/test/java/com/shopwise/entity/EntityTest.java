package com.shopwise.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class EntityTest {

    // ========== CLIENT TESTS ==========
    
    @Test
    @DisplayName("Client - Builder et Getters")
    void client_BuilderAndGetters() {
        Client client = Client.builder()
                .id(1L)
                .firstName("Marie")
                .lastName("Dupont")
                .email("marie@email.com")
                .phone("0612345678")
                .loyaltyPoints(100)
                .build();

        assertThat(client.getId()).isEqualTo(1L);
        assertThat(client.getFirstName()).isEqualTo("Marie");
        assertThat(client.getLastName()).isEqualTo("Dupont");
        assertThat(client.getEmail()).isEqualTo("marie@email.com");
        assertThat(client.getPhone()).isEqualTo("0612345678");
        assertThat(client.getLoyaltyPoints()).isEqualTo(100);
    }

    @Test
    @DisplayName("Client - Setters")
    void client_Setters() {
        Client client = new Client();
        client.setId(1L);
        client.setFirstName("Jean");
        client.setLastName("Martin");
        client.setEmail("jean@email.com");
        client.setPhone("0698765432");
        client.setLoyaltyPoints(50);

        assertThat(client.getId()).isEqualTo(1L);
        assertThat(client.getFirstName()).isEqualTo("Jean");
        assertThat(client.getLastName()).isEqualTo("Martin");
        assertThat(client.getEmail()).isEqualTo("jean@email.com");
        assertThat(client.getPhone()).isEqualTo("0698765432");
        assertThat(client.getLoyaltyPoints()).isEqualTo(50);
    }

    @Test
    @DisplayName("Client - getFullName")
    void client_GetFullName() {
        Client client = Client.builder()
                .firstName("Marie")
                .lastName("Dupont")
                .build();

        assertThat(client.getFullName()).isEqualTo("Marie Dupont");
    }

    @Test
    @DisplayName("Client - onCreate initialise les valeurs")
    void client_OnCreate() {
        Client client = new Client();
        client.setFirstName("Test");
        client.setLastName("User");
        client.setEmail("test@email.com");
        
        client.onCreate();

        assertThat(client.getCreatedAt()).isNotNull();
        assertThat(client.getUpdatedAt()).isNotNull();
        assertThat(client.getLoyaltyPoints()).isEqualTo(0);
    }

    @Test
    @DisplayName("Client - onCreate ne reset pas les points existants")
    void client_OnCreate_WithExistingPoints() {
        Client client = new Client();
        client.setLoyaltyPoints(100);
        
        client.onCreate();

        assertThat(client.getLoyaltyPoints()).isEqualTo(100);
    }

    @Test
    @DisplayName("Client - onUpdate met à jour updatedAt")
    void client_OnUpdate() {
        Client client = new Client();
        client.onCreate();
        LocalDateTime initialUpdate = client.getUpdatedAt();
        
        // Petite pause pour avoir une différence de temps
        try { Thread.sleep(10); } catch (InterruptedException e) {}
        
        client.onUpdate();

        assertThat(client.getUpdatedAt()).isAfterOrEqualTo(initialUpdate);
    }

    @Test
    @DisplayName("Client - equals et hashCode")
    void client_EqualsAndHashCode() {
        Client client1 = Client.builder().id(1L).email("test@email.com").build();
        Client client2 = Client.builder().id(1L).email("test@email.com").build();
        Client client3 = Client.builder().id(2L).email("other@email.com").build();

        assertThat(client1).isEqualTo(client2);
        assertThat(client1).isNotEqualTo(client3);
        assertThat(client1.hashCode()).isEqualTo(client2.hashCode());
    }

    @Test
    @DisplayName("Client - toString")
    void client_ToString() {
        Client client = Client.builder()
                .id(1L)
                .firstName("Marie")
                .lastName("Dupont")
                .build();

        String toString = client.toString();
        assertThat(toString).contains("Marie");
        assertThat(toString).contains("Dupont");
    }

    @Test
    @DisplayName("Client - NoArgsConstructor")
    void client_NoArgsConstructor() {
        Client client = new Client();
        assertThat(client).isNotNull();
    }

    @Test
    @DisplayName("Client - AllArgsConstructor")
    void client_AllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        Client client = new Client(1L, "Marie", "Dupont", "marie@email.com", 
                "0612345678", 100, now, now, null, null);
        
        assertThat(client.getId()).isEqualTo(1L);
        assertThat(client.getFirstName()).isEqualTo("Marie");
    }

    // ========== SERVICE TESTS ==========

    @Test
    @DisplayName("Service - Builder et Getters")
    void service_BuilderAndGetters() {
        Service service = Service.builder()
                .id(1L)
                .name("Consultation")
                .description("Description")
                .durationMinutes(30)
                .pointsAwarded(10)
                .active(true)
                .build();

        assertThat(service.getId()).isEqualTo(1L);
        assertThat(service.getName()).isEqualTo("Consultation");
        assertThat(service.getDescription()).isEqualTo("Description");
        assertThat(service.getDurationMinutes()).isEqualTo(30);
        assertThat(service.getPointsAwarded()).isEqualTo(10);
        assertThat(service.getActive()).isTrue();
    }

    @Test
    @DisplayName("Service - Setters")
    void service_Setters() {
        Service service = new Service();
        service.setId(1L);
        service.setName("Premium");
        service.setDescription("Desc");
        service.setDurationMinutes(60);
        service.setPointsAwarded(25);
        service.setActive(false);

        assertThat(service.getName()).isEqualTo("Premium");
        assertThat(service.getDurationMinutes()).isEqualTo(60);
        assertThat(service.getActive()).isFalse();
    }

    @Test
    @DisplayName("Service - onCreate initialise les valeurs par défaut")
    void service_OnCreate() {
        Service service = new Service();
        service.setName("Test");
        
        service.onCreate();

        assertThat(service.getCreatedAt()).isNotNull();
        assertThat(service.getUpdatedAt()).isNotNull();
        assertThat(service.getActive()).isTrue();
        assertThat(service.getDurationMinutes()).isEqualTo(30);
        assertThat(service.getPointsAwarded()).isEqualTo(10);
    }

    @Test
    @DisplayName("Service - onCreate ne reset pas les valeurs existantes")
    void service_OnCreate_WithExistingValues() {
        Service service = new Service();
        service.setActive(false);
        service.setDurationMinutes(60);
        service.setPointsAwarded(50);
        
        service.onCreate();

        assertThat(service.getActive()).isFalse();
        assertThat(service.getDurationMinutes()).isEqualTo(60);
        assertThat(service.getPointsAwarded()).isEqualTo(50);
    }

    @Test
    @DisplayName("Service - onUpdate")
    void service_OnUpdate() {
        Service service = new Service();
        service.onCreate();
        
        service.onUpdate();

        assertThat(service.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Service - NoArgsConstructor")
    void service_NoArgsConstructor() {
        Service service = new Service();
        assertThat(service).isNotNull();
    }

    // ========== APPOINTMENT TESTS ==========

    @Test
    @DisplayName("Appointment - Builder et Getters")
    void appointment_BuilderAndGetters() {
        Client client = Client.builder().id(1L).firstName("Marie").lastName("Dupont").build();
        Service service = Service.builder().id(1L).name("Consultation").build();
        
        Appointment appointment = Appointment.builder()
                .id(1L)
                .client(client)
                .service(service)
                .appointmentDate(LocalDate.of(2025, 3, 1))
                .appointmentTime(LocalTime.of(10, 0))
                .status(AppointmentStatus.SCHEDULED)
                .notes("Test note")
                .build();

        assertThat(appointment.getId()).isEqualTo(1L);
        assertThat(appointment.getClient()).isEqualTo(client);
        assertThat(appointment.getService()).isEqualTo(service);
        assertThat(appointment.getAppointmentDate()).isEqualTo(LocalDate.of(2025, 3, 1));
        assertThat(appointment.getAppointmentTime()).isEqualTo(LocalTime.of(10, 0));
        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.SCHEDULED);
        assertThat(appointment.getNotes()).isEqualTo("Test note");
    }

    @Test
    @DisplayName("Appointment - Setters")
    void appointment_Setters() {
        Appointment appointment = new Appointment();
        Client client = new Client();
        Service service = new Service();
        
        appointment.setId(1L);
        appointment.setClient(client);
        appointment.setService(service);
        appointment.setAppointmentDate(LocalDate.of(2025, 3, 15));
        appointment.setAppointmentTime(LocalTime.of(14, 30));
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setNotes("Note");

        assertThat(appointment.getId()).isEqualTo(1L);
        assertThat(appointment.getClient()).isEqualTo(client);
        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.COMPLETED);
    }

    @Test
    @DisplayName("Appointment - getAppointmentDateTime")
    void appointment_GetAppointmentDateTime() {
        Appointment appointment = Appointment.builder()
                .appointmentDate(LocalDate.of(2025, 3, 1))
                .appointmentTime(LocalTime.of(10, 30))
                .build();

        LocalDateTime dateTime = appointment.getAppointmentDateTime();

        assertThat(dateTime).isEqualTo(LocalDateTime.of(2025, 3, 1, 10, 30));
    }

    @Test
    @DisplayName("Appointment - onCreate initialise le statut par défaut")
    void appointment_OnCreate() {
        Appointment appointment = new Appointment();
        
        appointment.onCreate();

        assertThat(appointment.getCreatedAt()).isNotNull();
        assertThat(appointment.getUpdatedAt()).isNotNull();
        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.SCHEDULED);
    }

    @Test
    @DisplayName("Appointment - onCreate ne reset pas le statut existant")
    void appointment_OnCreate_WithExistingStatus() {
        Appointment appointment = new Appointment();
        appointment.setStatus(AppointmentStatus.COMPLETED);
        
        appointment.onCreate();

        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.COMPLETED);
    }

    @Test
    @DisplayName("Appointment - onUpdate")
    void appointment_OnUpdate() {
        Appointment appointment = new Appointment();
        appointment.onCreate();
        
        appointment.onUpdate();

        assertThat(appointment.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Appointment - NoArgsConstructor")
    void appointment_NoArgsConstructor() {
        Appointment appointment = new Appointment();
        assertThat(appointment).isNotNull();
    }

    // ========== POINT TRANSACTION TESTS ==========

    @Test
    @DisplayName("PointTransaction - Builder et Getters")
    void pointTransaction_BuilderAndGetters() {
        Client client = Client.builder().id(1L).build();
        Appointment appointment = Appointment.builder().id(1L).build();
        
        PointTransaction transaction = PointTransaction.builder()
                .id(1L)
                .client(client)
                .appointment(appointment)
                .points(10)
                .transactionType(TransactionType.EARNED)
                .description("Points gagnés")
                .build();

        assertThat(transaction.getId()).isEqualTo(1L);
        assertThat(transaction.getClient()).isEqualTo(client);
        assertThat(transaction.getAppointment()).isEqualTo(appointment);
        assertThat(transaction.getPoints()).isEqualTo(10);
        assertThat(transaction.getTransactionType()).isEqualTo(TransactionType.EARNED);
        assertThat(transaction.getDescription()).isEqualTo("Points gagnés");
    }

    @Test
    @DisplayName("PointTransaction - Setters")
    void pointTransaction_Setters() {
        PointTransaction transaction = new PointTransaction();
        Client client = new Client();
        
        transaction.setId(1L);
        transaction.setClient(client);
        transaction.setPoints(-50);
        transaction.setTransactionType(TransactionType.REDEEMED);
        transaction.setDescription("Utilisé");

        assertThat(transaction.getPoints()).isEqualTo(-50);
        assertThat(transaction.getTransactionType()).isEqualTo(TransactionType.REDEEMED);
    }

    @Test
    @DisplayName("PointTransaction - onCreate")
    void pointTransaction_OnCreate() {
        PointTransaction transaction = new PointTransaction();
        
        transaction.onCreate();

        assertThat(transaction.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("PointTransaction - NoArgsConstructor")
    void pointTransaction_NoArgsConstructor() {
        PointTransaction transaction = new PointTransaction();
        assertThat(transaction).isNotNull();
    }

    // ========== ENUM TESTS ==========

    @Test
    @DisplayName("AppointmentStatus - getLabel")
    void appointmentStatus_GetLabel() {
        assertThat(AppointmentStatus.SCHEDULED.getLabel()).isEqualTo("Planifié");
        assertThat(AppointmentStatus.COMPLETED.getLabel()).isEqualTo("Honoré");
        assertThat(AppointmentStatus.CANCELLED.getLabel()).isEqualTo("Annulé");
    }

    @Test
    @DisplayName("AppointmentStatus - values")
    void appointmentStatus_Values() {
        AppointmentStatus[] values = AppointmentStatus.values();
        assertThat(values).hasSize(3);
        assertThat(values).contains(AppointmentStatus.SCHEDULED, AppointmentStatus.COMPLETED, AppointmentStatus.CANCELLED);
    }

    @Test
    @DisplayName("AppointmentStatus - valueOf")
    void appointmentStatus_ValueOf() {
        assertThat(AppointmentStatus.valueOf("SCHEDULED")).isEqualTo(AppointmentStatus.SCHEDULED);
        assertThat(AppointmentStatus.valueOf("COMPLETED")).isEqualTo(AppointmentStatus.COMPLETED);
        assertThat(AppointmentStatus.valueOf("CANCELLED")).isEqualTo(AppointmentStatus.CANCELLED);
    }

    @Test
    @DisplayName("TransactionType - getLabel")
    void transactionType_GetLabel() {
        assertThat(TransactionType.EARNED.getLabel()).isEqualTo("Points gagnés");
        assertThat(TransactionType.REDEEMED.getLabel()).isEqualTo("Points utilisés");
        assertThat(TransactionType.ADJUSTMENT.getLabel()).isEqualTo("Ajustement");
    }

    @Test
    @DisplayName("TransactionType - values")
    void transactionType_Values() {
        TransactionType[] values = TransactionType.values();
        assertThat(values).hasSize(3);
        assertThat(values).contains(TransactionType.EARNED, TransactionType.REDEEMED, TransactionType.ADJUSTMENT);
    }

    @Test
    @DisplayName("TransactionType - valueOf")
    void transactionType_ValueOf() {
        assertThat(TransactionType.valueOf("EARNED")).isEqualTo(TransactionType.EARNED);
        assertThat(TransactionType.valueOf("REDEEMED")).isEqualTo(TransactionType.REDEEMED);
        assertThat(TransactionType.valueOf("ADJUSTMENT")).isEqualTo(TransactionType.ADJUSTMENT);
    }
}
