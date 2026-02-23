package com.shopwise.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopwise.dto.ClientDTO;
import com.shopwise.exception.ResourceNotFoundException;
import com.shopwise.service.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClientController.class)
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientService clientService;

    @Autowired
    private ObjectMapper objectMapper;

    private ClientDTO clientDTO;

    @BeforeEach
    void setUp() {
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
    @DisplayName("GET /api/clients - Retourne tous les clients")
    void getAllClients_ReturnsClientList() throws Exception {
        List<ClientDTO> clients = Arrays.asList(clientDTO);
        when(clientService.getAllClients()).thenReturn(clients);

        mockMvc.perform(get("/api/clients"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName", is("Marie")));

        verify(clientService).getAllClients();
    }

    @Test
    @DisplayName("GET /api/clients/{id} - Retourne le client")
    void getClientById_WhenExists_ReturnsClient() throws Exception {
        when(clientService.getClientById(1L)).thenReturn(clientDTO);

        mockMvc.perform(get("/api/clients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("Marie")))
                .andExpect(jsonPath("$.email", is("marie.dupont@email.com")));
    }

    @Test
    @DisplayName("GET /api/clients/{id} - Retourne 404 si non trouvé")
    void getClientById_WhenNotFound_Returns404() throws Exception {
        when(clientService.getClientById(999L)).thenThrow(new ResourceNotFoundException("Client", 999L));

        mockMvc.perform(get("/api/clients/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/clients/email/{email} - Retourne le client par email")
    void getClientByEmail_ReturnsClient() throws Exception {
        when(clientService.getClientByEmail("marie.dupont@email.com")).thenReturn(clientDTO);

        mockMvc.perform(get("/api/clients/email/marie.dupont@email.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("marie.dupont@email.com")));
    }

    @Test
    @DisplayName("GET /api/clients/search - Recherche des clients")
    void searchClients_ReturnsMatchingClients() throws Exception {
        when(clientService.searchClients("dup")).thenReturn(Arrays.asList(clientDTO));

        mockMvc.perform(get("/api/clients/search").param("query", "dup"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("POST /api/clients - Crée un nouveau client")
    void createClient_WithValidData_ReturnsCreatedClient() throws Exception {
        ClientDTO newClient = ClientDTO.builder()
                .firstName("Sophie")
                .lastName("Bernard")
                .email("sophie@email.com")
                .build();

        ClientDTO savedClient = ClientDTO.builder()
                .id(2L)
                .firstName("Sophie")
                .lastName("Bernard")
                .email("sophie@email.com")
                .loyaltyPoints(0)
                .build();

        when(clientService.createClient(any(ClientDTO.class))).thenReturn(savedClient);

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newClient)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.firstName", is("Sophie")));
    }

    @Test
    @DisplayName("POST /api/clients - Retourne 400 si données invalides")
    void createClient_WithInvalidData_Returns400() throws Exception {
        ClientDTO invalidClient = ClientDTO.builder()
                .firstName("")  // Invalide
                .lastName("Test")
                .email("invalid")  // Invalide
                .build();

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidClient)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/clients/{id} - Met à jour le client")
    void updateClient_WithValidData_ReturnsUpdatedClient() throws Exception {
        ClientDTO updatedClient = ClientDTO.builder()
                .firstName("Marie-Claire")
                .lastName("Dupont")
                .email("marie.dupont@email.com")
                .build();

        when(clientService.updateClient(anyLong(), any(ClientDTO.class))).thenReturn(clientDTO);

        mockMvc.perform(put("/api/clients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedClient)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/clients/{id} - Supprime le client")
    void deleteClient_ReturnsNoContent() throws Exception {
        doNothing().when(clientService).deleteClient(1L);

        mockMvc.perform(delete("/api/clients/1"))
                .andExpect(status().isNoContent());

        verify(clientService).deleteClient(1L);
    }
}
