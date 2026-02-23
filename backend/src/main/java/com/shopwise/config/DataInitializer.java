package com.shopwise.config;

import com.shopwise.entity.*;
import com.shopwise.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private final ClientRepository clientRepository;
    private final ServiceRepository serviceRepository;
    private final AppointmentRepository appointmentRepository;
    private final PointTransactionRepository transactionRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (clientRepository.count() > 0) {
            log.info("Base de données déjà initialisée, skip...");
            return;
        }

        log.info("Initialisation des données de test...");

        // Création des services
        List<Service> services = createServices();
        
        // Création des clients
        List<Client> clients = createClients();
        
        // Création des rendez-vous
        createAppointments(clients, services);

        log.info("Données de test initialisées avec succès !");
    }

    private List<Service> createServices() {
        List<Service> services = Arrays.asList(
                Service.builder()
                        .name("Consultation standard")
                        .description("Consultation de base avec le commerçant")
                        .durationMinutes(30)
                        .pointsAwarded(10)
                        .active(true)
                        .build(),
                Service.builder()
                        .name("Consultation premium")
                        .description("Consultation approfondie avec conseils personnalisés")
                        .durationMinutes(60)
                        .pointsAwarded(25)
                        .active(true)
                        .build(),
                Service.builder()
                        .name("Retrait commande")
                        .description("Retrait de commande Click & Collect")
                        .durationMinutes(15)
                        .pointsAwarded(5)
                        .active(true)
                        .build(),
                Service.builder()
                        .name("Livraison à domicile")
                        .description("Préparation pour livraison à domicile")
                        .durationMinutes(45)
                        .pointsAwarded(15)
                        .active(true)
                        .build(),
                Service.builder()
                        .name("Atelier découverte")
                        .description("Atelier de découverte des produits")
                        .durationMinutes(90)
                        .pointsAwarded(50)
                        .active(true)
                        .build()
        );

        return serviceRepository.saveAll(services);
    }

    private List<Client> createClients() {
        List<Client> clients = Arrays.asList(
                Client.builder()
                        .firstName("Marie")
                        .lastName("Dupont")
                        .email("marie.dupont@email.com")
                        .phone("0612345678")
                        .loyaltyPoints(150)
                        .build(),
                Client.builder()
                        .firstName("Jean")
                        .lastName("Martin")
                        .email("jean.martin@email.com")
                        .phone("0623456789")
                        .loyaltyPoints(75)
                        .build(),
                Client.builder()
                        .firstName("Sophie")
                        .lastName("Bernard")
                        .email("sophie.bernard@email.com")
                        .phone("0634567890")
                        .loyaltyPoints(200)
                        .build(),
                Client.builder()
                        .firstName("Pierre")
                        .lastName("Petit")
                        .email("pierre.petit@email.com")
                        .phone("0645678901")
                        .loyaltyPoints(30)
                        .build(),
                Client.builder()
                        .firstName("Isabelle")
                        .lastName("Durand")
                        .email("isabelle.durand@email.com")
                        .phone("0656789012")
                        .loyaltyPoints(0)
                        .build(),
                Client.builder()
                        .firstName("Luc")
                        .lastName("Moreau")
                        .email("luc.moreau@email.com")
                        .phone("0667890123")
                        .loyaltyPoints(125)
                        .build(),
                Client.builder()
                        .firstName("Emma")
                        .lastName("Laurent")
                        .email("emma.laurent@email.com")
                        .phone("0678901234")
                        .loyaltyPoints(45)
                        .build(),
                Client.builder()
                        .firstName("Thomas")
                        .lastName("Simon")
                        .email("thomas.simon@email.com")
                        .phone("0689012345")
                        .loyaltyPoints(300)
                        .build()
        );

        return clientRepository.saveAll(clients);
    }

    private void createAppointments(List<Client> clients, List<Service> services) {
        LocalDate today = LocalDate.now();

        // Rendez-vous passés (honorés)
        createCompletedAppointment(clients.get(0), services.get(0), today.minusDays(10), LocalTime.of(9, 0));
        createCompletedAppointment(clients.get(0), services.get(1), today.minusDays(5), LocalTime.of(14, 0));
        createCompletedAppointment(clients.get(1), services.get(0), today.minusDays(7), LocalTime.of(10, 30));
        createCompletedAppointment(clients.get(2), services.get(2), today.minusDays(3), LocalTime.of(11, 0));

        // Rendez-vous annulés
        Appointment cancelled1 = Appointment.builder()
                .client(clients.get(1))
                .service(services.get(1))
                .appointmentDate(today.minusDays(6))
                .appointmentTime(LocalTime.of(16, 0))
                .status(AppointmentStatus.CANCELLED)
                .notes("Client indisponible")
                .build();
        appointmentRepository.save(cancelled1);

        // Rendez-vous à venir (planifiés)
        Appointment scheduled1 = Appointment.builder()
                .client(clients.get(0))
                .service(services.get(4))
                .appointmentDate(today.plusDays(5))
                .appointmentTime(LocalTime.of(10, 0))
                .status(AppointmentStatus.SCHEDULED)
                .notes("Atelier spécial")
                .build();
        appointmentRepository.save(scheduled1);

        Appointment scheduled2 = Appointment.builder()
                .client(clients.get(2))
                .service(services.get(0))
                .appointmentDate(today.plusDays(7))
                .appointmentTime(LocalTime.of(14, 30))
                .status(AppointmentStatus.SCHEDULED)
                .build();
        appointmentRepository.save(scheduled2);

        Appointment scheduled3 = Appointment.builder()
                .client(clients.get(5))
                .service(services.get(1))
                .appointmentDate(today.plusDays(10))
                .appointmentTime(LocalTime.of(11, 0))
                .status(AppointmentStatus.SCHEDULED)
                .notes("Nouveau client fidélisé")
                .build();
        appointmentRepository.save(scheduled3);
    }

    private void createCompletedAppointment(Client client, Service service, LocalDate date, LocalTime time) {
        Appointment appointment = Appointment.builder()
                .client(client)
                .service(service)
                .appointmentDate(date)
                .appointmentTime(time)
                .status(AppointmentStatus.COMPLETED)
                .build();
        appointment = appointmentRepository.save(appointment);

        // Créer la transaction de points associée
        PointTransaction transaction = PointTransaction.builder()
                .client(client)
                .appointment(appointment)
                .points(service.getPointsAwarded())
                .transactionType(TransactionType.EARNED)
                .description("Points gagnés - " + service.getName())
                .build();
        transactionRepository.save(transaction);
    }
}
