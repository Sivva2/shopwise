package com.shopwise.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopwise.dto.ServiceDTO;
import com.shopwise.exception.ResourceNotFoundException;
import com.shopwise.service.ServiceService;
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

@WebMvcTest(ServiceController.class)
class ServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServiceService serviceService;

    @Autowired
    private ObjectMapper objectMapper;

    private ServiceDTO serviceDTO;

    @BeforeEach
    void setUp() {
        serviceDTO = ServiceDTO.builder()
                .id(1L)
                .name("Consultation")
                .description("Consultation standard")
                .durationMinutes(30)
                .pointsAwarded(10)
                .active(true)
                .build();
    }

    @Test
    @DisplayName("GET /api/services - Retourne tous les services")
    void getAllServices_ReturnsServiceList() throws Exception {
        List<ServiceDTO> services = Arrays.asList(serviceDTO);
        when(serviceService.getAllServices()).thenReturn(services);

        mockMvc.perform(get("/api/services"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Consultation")));

        verify(serviceService).getAllServices();
    }

    @Test
    @DisplayName("GET /api/services/active - Retourne les services actifs")
    void getActiveServices_ReturnsActiveServices() throws Exception {
        when(serviceService.getActiveServices()).thenReturn(Arrays.asList(serviceDTO));

        mockMvc.perform(get("/api/services/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].active", is(true)));
    }

    @Test
    @DisplayName("GET /api/services/{id} - Retourne le service")
    void getServiceById_WhenExists_ReturnsService() throws Exception {
        when(serviceService.getServiceById(1L)).thenReturn(serviceDTO);

        mockMvc.perform(get("/api/services/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Consultation")));
    }

    @Test
    @DisplayName("GET /api/services/{id} - Retourne 404 si non trouvé")
    void getServiceById_WhenNotFound_Returns404() throws Exception {
        when(serviceService.getServiceById(999L)).thenThrow(new ResourceNotFoundException("Service", 999L));

        mockMvc.perform(get("/api/services/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/services - Crée un nouveau service")
    void createService_WithValidData_ReturnsCreatedService() throws Exception {
        ServiceDTO newService = ServiceDTO.builder()
                .name("Premium")
                .durationMinutes(60)
                .pointsAwarded(25)
                .active(true)
                .build();

        ServiceDTO savedService = ServiceDTO.builder()
                .id(2L)
                .name("Premium")
                .durationMinutes(60)
                .pointsAwarded(25)
                .active(true)
                .build();

        when(serviceService.createService(any(ServiceDTO.class))).thenReturn(savedService);

        mockMvc.perform(post("/api/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newService)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.name", is("Premium")));
    }

    @Test
    @DisplayName("PUT /api/services/{id} - Met à jour le service")
    void updateService_WithValidData_ReturnsUpdatedService() throws Exception {
        ServiceDTO updatedService = ServiceDTO.builder()
                .name("Consultation Premium")
                .durationMinutes(45)
                .pointsAwarded(15)
                .active(true)
                .build();

        when(serviceService.updateService(anyLong(), any(ServiceDTO.class))).thenReturn(serviceDTO);

        mockMvc.perform(put("/api/services/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedService)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/services/{id} - Supprime le service")
    void deleteService_ReturnsNoContent() throws Exception {
        doNothing().when(serviceService).deleteService(1L);

        mockMvc.perform(delete("/api/services/1"))
                .andExpect(status().isNoContent());

        verify(serviceService).deleteService(1L);
    }
}
