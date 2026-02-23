package com.shopwise.service;

import com.shopwise.dto.ClientDTO;
import com.shopwise.entity.Client;
import com.shopwise.exception.BusinessException;
import com.shopwise.exception.ResourceNotFoundException;
import com.shopwise.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientService {

    private final ClientRepository clientRepository;

    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ClientDTO getClientById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
        return toDTO(client);
    }

    public ClientDTO getClientByEmail(String email) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Client avec l'email " + email + " non trouvé"));
        return toDTO(client);
    }

    public ClientDTO createClient(ClientDTO clientDTO) {
        if (clientRepository.existsByEmail(clientDTO.getEmail())) {
            throw new BusinessException("Un client avec cet email existe déjà");
        }

        Client client = toEntity(clientDTO);
        client = clientRepository.save(client);
        return toDTO(client);
    }

    public ClientDTO updateClient(Long id, ClientDTO clientDTO) {
        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));

        // Vérifier si l'email est déjà utilisé par un autre client
        if (!existingClient.getEmail().equals(clientDTO.getEmail()) 
                && clientRepository.existsByEmail(clientDTO.getEmail())) {
            throw new BusinessException("Un client avec cet email existe déjà");
        }

        existingClient.setFirstName(clientDTO.getFirstName());
        existingClient.setLastName(clientDTO.getLastName());
        existingClient.setEmail(clientDTO.getEmail());
        existingClient.setPhone(clientDTO.getPhone());

        existingClient = clientRepository.save(existingClient);
        return toDTO(existingClient);
    }

    public void deleteClient(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Client", id);
        }
        clientRepository.deleteById(id);
    }

    public List<ClientDTO> searchClients(String query) {
        return clientRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(query, query)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public void updateLoyaltyPoints(Long clientId, int pointsDelta) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", clientId));
        
        int newPoints = client.getLoyaltyPoints() + pointsDelta;
        if (newPoints < 0) {
            throw new BusinessException("Le solde de points ne peut pas être négatif");
        }
        
        client.setLoyaltyPoints(newPoints);
        clientRepository.save(client);
    }

    // Mapper Entity -> DTO
    public ClientDTO toDTO(Client client) {
        return ClientDTO.builder()
                .id(client.getId())
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .email(client.getEmail())
                .phone(client.getPhone())
                .loyaltyPoints(client.getLoyaltyPoints())
                .createdAt(client.getCreatedAt())
                .updatedAt(client.getUpdatedAt())
                .build();
    }

    // Mapper DTO -> Entity
    public Client toEntity(ClientDTO dto) {
        return Client.builder()
                .id(dto.getId())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .loyaltyPoints(dto.getLoyaltyPoints() != null ? dto.getLoyaltyPoints() : 0)
                .build();
    }
}
