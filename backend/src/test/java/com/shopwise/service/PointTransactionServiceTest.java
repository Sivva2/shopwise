package com.shopwise.service;

import com.shopwise.dto.PointTransactionDTO;
import com.shopwise.entity.*;
import com.shopwise.exception.BusinessException;
import com.shopwise.exception.ResourceNotFoundException;
import com.shopwise.repository.ClientRepository;
import com.shopwise.repository.PointTransactionRepository;
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
class PointTransactionServiceTest {

    @Mock
    private PointTransactionRepository transactionRepository;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private PointTransactionService transactionService;

    private Client client;
    private Service service;
    private Appointment appointment;
    private PointTransaction transaction;

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
                .pointsAwarded(10)
                .build();

        appointment = Appointment.builder()
                .id(1L)
                .client(client)
                .service(service)
                .appointmentDate(LocalDate.now())
                .appointmentTime(LocalTime.of(10, 0))
                .status(AppointmentStatus.COMPLETED)
                .build();

        transaction = PointTransaction.builder()
                .id(1L)
                .client(client)
                .appointment(appointment)
                .points(10)
                .transactionType(TransactionType.EARNED)
                .description("Points gagnés")
                .build();
    }

    @Test
    @DisplayName("getTransactionsByClient - Retourne les transactions du client")
    void getTransactionsByClient_ReturnsClientTransactions() {
        when(clientRepository.existsById(1L)).thenReturn(true);
        when(transactionRepository.findByClientIdOrderByCreatedAtDesc(1L)).thenReturn(Arrays.asList(transaction));

        List<PointTransactionDTO> result = transactionService.getTransactionsByClient(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPoints()).isEqualTo(10);
    }

    @Test
    @DisplayName("getTransactionsByClient - Lance exception si client n'existe pas")
    void getTransactionsByClient_WhenClientNotFound_ThrowsException() {
        when(clientRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> transactionService.getTransactionsByClient(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("getTransactionById - Retourne la transaction")
    void getTransactionById_WhenExists_ReturnsTransaction() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        PointTransactionDTO result = transactionService.getTransactionById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getPoints()).isEqualTo(10);
    }

    @Test
    @DisplayName("getTransactionById - Lance exception si transaction n'existe pas")
    void getTransactionById_WhenNotFound_ThrowsException() {
        when(transactionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.getTransactionById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("awardPointsForAppointment - Attribue les points correctement")
    void awardPointsForAppointment_AwardsPointsCorrectly() {
        when(transactionRepository.existsByAppointmentId(1L)).thenReturn(false);
        when(transactionRepository.save(any(PointTransaction.class))).thenReturn(transaction);
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        transactionService.awardPointsForAppointment(appointment);

        verify(transactionRepository).save(any(PointTransaction.class));
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    @DisplayName("awardPointsForAppointment - Lance exception si déjà attribués")
    void awardPointsForAppointment_WhenAlreadyAwarded_ThrowsException() {
        when(transactionRepository.existsByAppointmentId(1L)).thenReturn(true);

        assertThatThrownBy(() -> transactionService.awardPointsForAppointment(appointment))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("redeemPoints - Utilise les points correctement")
    void redeemPoints_WithSufficientBalance_RedeemsPoints() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(transactionRepository.save(any(PointTransaction.class))).thenReturn(transaction);
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        PointTransactionDTO result = transactionService.redeemPoints(1L, 50, "Réduction");

        assertThat(result).isNotNull();
        verify(transactionRepository).save(any(PointTransaction.class));
    }

    @Test
    @DisplayName("redeemPoints - Lance exception si points négatifs ou nuls")
    void redeemPoints_WithInvalidPoints_ThrowsException() {
        assertThatThrownBy(() -> transactionService.redeemPoints(1L, 0, "Test"))
                .isInstanceOf(BusinessException.class);

        assertThatThrownBy(() -> transactionService.redeemPoints(1L, -10, "Test"))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("redeemPoints - Lance exception si solde insuffisant")
    void redeemPoints_WithInsufficientBalance_ThrowsException() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        assertThatThrownBy(() -> transactionService.redeemPoints(1L, 200, "Test"))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("adjustPoints - Ajuste les points positivement")
    void adjustPoints_WithPositiveValue_AdjustsCorrectly() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(transactionRepository.save(any(PointTransaction.class))).thenReturn(transaction);
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        PointTransactionDTO result = transactionService.adjustPoints(1L, 50, "Bonus");

        assertThat(result).isNotNull();
        verify(transactionRepository).save(any(PointTransaction.class));
    }

    @Test
    @DisplayName("adjustPoints - Ajuste les points négativement")
    void adjustPoints_WithNegativeValue_AdjustsCorrectly() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(transactionRepository.save(any(PointTransaction.class))).thenReturn(transaction);
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        PointTransactionDTO result = transactionService.adjustPoints(1L, -50, "Correction");

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("adjustPoints - Lance exception si résultat négatif")
    void adjustPoints_WhenResultNegative_ThrowsException() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        assertThatThrownBy(() -> transactionService.adjustPoints(1L, -200, "Test"))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("getClientBalance - Retourne le solde du client")
    void getClientBalance_ReturnsBalance() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        Integer result = transactionService.getClientBalance(1L);

        assertThat(result).isEqualTo(100);
    }

    @Test
    @DisplayName("getTotalEarnedPoints - Retourne le total des points gagnés")
    void getTotalEarnedPoints_ReturnsTotalEarned() {
        when(transactionRepository.sumPointsByClientIdAndType(1L, TransactionType.EARNED)).thenReturn(150);

        Integer result = transactionService.getTotalEarnedPoints(1L);

        assertThat(result).isEqualTo(150);
    }

    @Test
    @DisplayName("getTotalRedeemedPoints - Retourne le total des points utilisés")
    void getTotalRedeemedPoints_ReturnsTotalRedeemed() {
        when(transactionRepository.sumPointsByClientIdAndType(1L, TransactionType.REDEEMED)).thenReturn(-50);

        Integer result = transactionService.getTotalRedeemedPoints(1L);

        assertThat(result).isEqualTo(50);
    }

    @Test
    @DisplayName("getTotalRedeemedPoints - Gère null")
    void getTotalRedeemedPoints_WhenNull_ReturnsZero() {
        when(transactionRepository.sumPointsByClientIdAndType(1L, TransactionType.REDEEMED)).thenReturn(null);

        Integer result = transactionService.getTotalRedeemedPoints(1L);

        assertThat(result).isEqualTo(0);
    }

    @Test
    @DisplayName("toDTO - Convertit correctement")
    void toDTO_ConvertsCorrectly() {
        PointTransactionDTO result = transactionService.toDTO(transaction);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getClientId()).isEqualTo(1L);
        assertThat(result.getClientName()).isEqualTo("Marie Dupont");
        assertThat(result.getPoints()).isEqualTo(10);
        assertThat(result.getTransactionType()).isEqualTo(TransactionType.EARNED);
    }

    @Test
    @DisplayName("toDTO - Gère appointment null")
    void toDTO_WithNullAppointment_HandlesCorrectly() {
        transaction.setAppointment(null);

        PointTransactionDTO result = transactionService.toDTO(transaction);

        assertThat(result.getAppointmentId()).isNull();
    }
}
