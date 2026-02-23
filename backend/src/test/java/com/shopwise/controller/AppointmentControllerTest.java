package com.shopwise.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.shopwise.dto.AppointmentDTO;
import com.shopwise.dto.UpdateStatusDTO;
import com.shopwise.entity.AppointmentStatus;
import com.shopwise.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppointmentController.class)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppointmentService appointmentService;

    private ObjectMapper objectMapper;
    private AppointmentDTO appointmentDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        appointmentDTO = AppointmentDTO.builder()
                .id(1L)
                .clientId(1L)
                .clientName("Marie Dupont")
                .serviceId(1L)
                .serviceName("Consultation")
                .appointmentDate(LocalDate.now().plusDays(1))
                .appointmentTime(LocalTime.of(10, 0))
                .status(AppointmentStatus.SCHEDULED)
                .build();
    }

    @Test
    @DisplayName("GET /api/appointments - Retourne tous les RDV")
    void getAllAppointments_ReturnsAll() throws Exception {
        when(appointmentService.getAllAppointments()).thenReturn(Arrays.asList(appointmentDTO));

        mockMvc.perform(get("/api/appointments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].clientName", is("Marie Dupont")));
    }

    @Test
    @DisplayName("GET /api/appointments - Filtre par clientId")
    void getAllAppointments_WithClientFilter_FiltersCorrectly() throws Exception {
        when(appointmentService.getAppointmentsByFilters(1L, null, null))
                .thenReturn(Arrays.asList(appointmentDTO));

        mockMvc.perform(get("/api/appointments").param("clientId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("GET /api/appointments/{id} - Retourne le RDV")
    void getAppointmentById_ReturnsAppointment() throws Exception {
        when(appointmentService.getAppointmentById(1L)).thenReturn(appointmentDTO);

        mockMvc.perform(get("/api/appointments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("SCHEDULED")));
    }

    @Test
    @DisplayName("GET /api/appointments/client/{clientId} - Retourne les RDV du client")
    void getAppointmentsByClient_ReturnsClientAppointments() throws Exception {
        when(appointmentService.getAppointmentsByClient(1L)).thenReturn(Arrays.asList(appointmentDTO));

        mockMvc.perform(get("/api/appointments/client/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("POST /api/appointments - Crée un nouveau RDV")
    void createAppointment_ReturnsCreatedAppointment() throws Exception {
        AppointmentDTO newAppointment = AppointmentDTO.builder()
                .clientId(1L)
                .serviceId(1L)
                .appointmentDate(LocalDate.now().plusDays(1))
                .appointmentTime(LocalTime.of(14, 0))
                .build();

        when(appointmentService.createAppointment(any(AppointmentDTO.class))).thenReturn(appointmentDTO);

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAppointment)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("PUT /api/appointments/{id} - Met à jour le RDV")
    void updateAppointment_ReturnsUpdatedAppointment() throws Exception {
        when(appointmentService.updateAppointment(anyLong(), any(AppointmentDTO.class))).thenReturn(appointmentDTO);

        mockMvc.perform(put("/api/appointments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appointmentDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /api/appointments/{id}/status - Met à jour le statut")
    void updateStatus_ReturnsUpdatedAppointment() throws Exception {
        UpdateStatusDTO statusDTO = new UpdateStatusDTO(AppointmentStatus.COMPLETED);
        appointmentDTO.setStatus(AppointmentStatus.COMPLETED);

        when(appointmentService.updateStatus(1L, AppointmentStatus.COMPLETED)).thenReturn(appointmentDTO);

        mockMvc.perform(patch("/api/appointments/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("COMPLETED")));
    }

    @Test
    @DisplayName("DELETE /api/appointments/{id} - Supprime le RDV")
    void deleteAppointment_ReturnsNoContent() throws Exception {
        doNothing().when(appointmentService).deleteAppointment(1L);

        mockMvc.perform(delete("/api/appointments/1"))
                .andExpect(status().isNoContent());

        verify(appointmentService).deleteAppointment(1L);
    }
}
