package com.shopwise.service;

import com.shopwise.dto.ClientDTO;
import com.shopwise.entity.Client;
import com.shopwise.exception.BusinessException;
import com.shopwise.exception.ResourceNotFoundException;
import com.shopwise.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    private Client client;
    private ClientDTO clientDTO;

    @BeforeEach
    void setUp() {
        client = Client.builder()
                .id(1L)
                .firstName("Marie")
                .lastName("Dupont")
                .email("marie.dupont@email.com")
                .phone("0612345678")
                .loyaltyPoints(100)
                .build();

        clientDTO = ClientDTO.builder()
                .id(1L)
                .firstName("Marie")
                .lastName("Dupont")
                .email("marie.dupont@email.com")
                .phone("0612345678")
                .loyaltyPoints(100)
                .build();
    }

    @Test
    @DisplayName("getAllClients - Retourne la liste de tous les clients")
    void getAllClients_ReturnsAllClients() {
        Client client2 = Client.builder()
                .id(2L)
                .firstName("Jean")
                .lastName("Martin")
                .email("jean.martin@email.com")
                .loyaltyPoints(50)
                .build();

        when(clientRepository.findAll()).thenReturn(Arrays.asList(client, client2));

        List<ClientDTO> result = clientService.getAllClients();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getFirstName()).isEqualTo("Marie");
        verify(clientRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getClientById - Retourne le client quand il existe")
    void getClientById_WhenClientExists_ReturnsClient() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        ClientDTO result = clientService.getClientById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("marie.dupont@email.com");
    }

    @Test
    @DisplayName("getClientById - Lance exception quand client n'existe pas")
    void getClientById_WhenClientNotFound_ThrowsException() {
        when(clientRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.getClientById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("getClientByEmail - Retourne le client quand email existe")
    void getClientByEmail_WhenEmailExists_ReturnsClient() {
        when(clientRepository.findByEmail("marie.dupont@email.com")).thenReturn(Optional.of(client));

        ClientDTO result = clientService.getClientByEmail("marie.dupont@email.com");

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("marie.dupont@email.com");
    }

    @Test
    @DisplayName("createClient - Crée un nouveau client avec succès")
    void createClient_WithValidData_ReturnsCreatedClient() {
        ClientDTO newClientDTO = ClientDTO.builder()
                .firstName("Sophie")
                .lastName("Bernard")
                .email("sophie.bernard@email.com")
                .build();

        Client savedClient = Client.builder()
                .id(3L)
                .firstName("Sophie")
                .lastName("Bernard")
                .email("sophie.bernard@email.com")
                .loyaltyPoints(0)
                .build();

        when(clientRepository.existsByEmail("sophie.bernard@email.com")).thenReturn(false);
        when(clientRepository.save(any(Client.class))).thenReturn(savedClient);

        ClientDTO result = clientService.createClient(newClientDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(3L);
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    @DisplayName("createClient - Lance exception si email existe déjà")
    void createClient_WithExistingEmail_ThrowsException() {
        when(clientRepository.existsByEmail("marie.dupont@email.com")).thenReturn(true);

        assertThatThrownBy(() -> clientService.createClient(clientDTO))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("updateClient - Met à jour le client avec succès")
    void updateClient_WithValidData_ReturnsUpdatedClient() {
        ClientDTO updateDTO = ClientDTO.builder()
                .firstName("Marie-Claire")
                .lastName("Dupont")
                .email("marie.dupont@email.com")
                .phone("0699999999")
                .build();

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        ClientDTO result = clientService.updateClient(1L, updateDTO);

        assertThat(result).isNotNull();
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    @DisplayName("deleteClient - Supprime le client avec succès")
    void deleteClient_WhenClientExists_DeletesClient() {
        when(clientRepository.existsById(1L)).thenReturn(true);
        doNothing().when(clientRepository).deleteById(1L);

        clientService.deleteClient(1L);

        verify(clientRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteClient - Lance exception si client n'existe pas")
    void deleteClient_WhenClientNotFound_ThrowsException() {
        when(clientRepository.existsById(anyLong())).thenReturn(false);

        assertThatThrownBy(() -> clientService.deleteClient(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("searchClients - Retourne les clients correspondants")
    void searchClients_WithQuery_ReturnsMatchingClients() {
        when(clientRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase("dup", "dup"))
                .thenReturn(Arrays.asList(client));

        List<ClientDTO> result = clientService.searchClients("dup");

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("updateLoyaltyPoints - Met à jour les points avec succès")
    void updateLoyaltyPoints_WithValidDelta_UpdatesPoints() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        clientService.updateLoyaltyPoints(1L, 50);

        verify(clientRepository).save(any(Client.class));
    }

    @Test
    @DisplayName("updateLoyaltyPoints - Lance exception si solde devient négatif")
    void updateLoyaltyPoints_WhenResultNegative_ThrowsException() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        assertThatThrownBy(() -> clientService.updateLoyaltyPoints(1L, -200))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("toDTO - Convertit correctement Entity en DTO")
    void toDTO_ConvertsEntityToDTO() {
        ClientDTO result = clientService.toDTO(client);

        assertThat(result.getId()).isEqualTo(client.getId());
        assertThat(result.getFirstName()).isEqualTo(client.getFirstName());
        assertThat(result.getEmail()).isEqualTo(client.getEmail());
    }

    @Test
    @DisplayName("toEntity - Convertit correctement DTO en Entity")
    void toEntity_ConvertsDTOToEntity() {
        Client result = clientService.toEntity(clientDTO);

        assertThat(result.getFirstName()).isEqualTo(clientDTO.getFirstName());
        assertThat(result.getEmail()).isEqualTo(clientDTO.getEmail());
    }

    @Test
    @DisplayName("toEntity - Gère les points null")
    void toEntity_WithNullPoints_SetsZero() {
        clientDTO.setLoyaltyPoints(null);
        
        Client result = clientService.toEntity(clientDTO);

        assertThat(result.getLoyaltyPoints()).isEqualTo(0);
    }
}
