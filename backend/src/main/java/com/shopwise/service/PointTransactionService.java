package com.shopwise.service;

import com.shopwise.dto.PointTransactionDTO;
import com.shopwise.entity.*;
import com.shopwise.exception.BusinessException;
import com.shopwise.exception.ResourceNotFoundException;
import com.shopwise.repository.ClientRepository;
import com.shopwise.repository.PointTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PointTransactionService {

    private final PointTransactionRepository transactionRepository;
    private final ClientRepository clientRepository;

    public List<PointTransactionDTO> getTransactionsByClient(Long clientId) {
        if (!clientRepository.existsById(clientId)) {
            throw new ResourceNotFoundException("Client", clientId);
        }
        return transactionRepository.findByClientIdOrderByCreatedAtDesc(clientId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public PointTransactionDTO getTransactionById(Long id) {
        PointTransaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", id));
        return toDTO(transaction);
    }

    public void awardPointsForAppointment(Appointment appointment) {
        // Vérifier si des points n'ont pas déjà été attribués pour ce RDV
        if (transactionRepository.existsByAppointmentId(appointment.getId())) {
            throw new BusinessException("Des points ont déjà été attribués pour ce rendez-vous");
        }

        int pointsToAward = appointment.getService().getPointsAwarded();
        Client client = appointment.getClient();

        // Créer la transaction
        PointTransaction transaction = PointTransaction.builder()
                .client(client)
                .appointment(appointment)
                .points(pointsToAward)
                .transactionType(TransactionType.EARNED)
                .description("Points gagnés - " + appointment.getService().getName())
                .build();

        transactionRepository.save(transaction);

        // Mettre à jour le solde du client
        client.setLoyaltyPoints(client.getLoyaltyPoints() + pointsToAward);
        clientRepository.save(client);
    }

    public PointTransactionDTO redeemPoints(Long clientId, int points, String description) {
        if (points <= 0) {
            throw new BusinessException("Le nombre de points doit être positif");
        }

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", clientId));

        if (client.getLoyaltyPoints() < points) {
            throw new BusinessException("Solde de points insuffisant");
        }

        // Créer la transaction (points négatifs pour une utilisation)
        PointTransaction transaction = PointTransaction.builder()
                .client(client)
                .points(-points)
                .transactionType(TransactionType.REDEEMED)
                .description(description != null ? description : "Utilisation de points")
                .build();

        transaction = transactionRepository.save(transaction);

        // Mettre à jour le solde du client
        client.setLoyaltyPoints(client.getLoyaltyPoints() - points);
        clientRepository.save(client);

        return toDTO(transaction);
    }

    public PointTransactionDTO adjustPoints(Long clientId, int points, String description) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", clientId));

        int newBalance = client.getLoyaltyPoints() + points;
        if (newBalance < 0) {
            throw new BusinessException("L'ajustement rendrait le solde négatif");
        }

        // Créer la transaction d'ajustement
        PointTransaction transaction = PointTransaction.builder()
                .client(client)
                .points(points)
                .transactionType(TransactionType.ADJUSTMENT)
                .description(description != null ? description : "Ajustement de points")
                .build();

        transaction = transactionRepository.save(transaction);

        // Mettre à jour le solde du client
        client.setLoyaltyPoints(newBalance);
        clientRepository.save(client);

        return toDTO(transaction);
    }

    public Integer getClientBalance(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", clientId));
        return client.getLoyaltyPoints();
    }

    public Integer getTotalEarnedPoints(Long clientId) {
        return transactionRepository.sumPointsByClientIdAndType(clientId, TransactionType.EARNED);
    }

    public Integer getTotalRedeemedPoints(Long clientId) {
        Integer redeemed = transactionRepository.sumPointsByClientIdAndType(clientId, TransactionType.REDEEMED);
        return redeemed != null ? Math.abs(redeemed) : 0;
    }

    // Mapper Entity -> DTO
    public PointTransactionDTO toDTO(PointTransaction transaction) {
        return PointTransactionDTO.builder()
                .id(transaction.getId())
                .clientId(transaction.getClient().getId())
                .clientName(transaction.getClient().getFullName())
                .appointmentId(transaction.getAppointment() != null ? transaction.getAppointment().getId() : null)
                .points(transaction.getPoints())
                .transactionType(transaction.getTransactionType())
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
