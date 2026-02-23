package com.shopwise.service;

import com.shopwise.dto.ServiceDTO;
import com.shopwise.entity.Service;
import com.shopwise.exception.ResourceNotFoundException;
import com.shopwise.repository.ServiceRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceServiceTest {

    @Mock
    private ServiceRepository serviceRepository;

    @InjectMocks
    private ServiceService serviceService;

    private Service service;
    private ServiceDTO serviceDTO;

    @BeforeEach
    void setUp() {
        service = Service.builder()
                .id(1L)
                .name("Consultation standard")
                .description("Consultation de base")
                .durationMinutes(30)
                .pointsAwarded(10)
                .active(true)
                .build();

        serviceDTO = ServiceDTO.builder()
                .id(1L)
                .name("Consultation standard")
                .description("Consultation de base")
                .durationMinutes(30)
                .pointsAwarded(10)
                .active(true)
                .build();
    }

    @Test
    @DisplayName("getAllServices - Retourne tous les services")
    void getAllServices_ReturnsAllServices() {
        Service service2 = Service.builder()
                .id(2L)
                .name("Consultation premium")
                .durationMinutes(60)
                .pointsAwarded(25)
                .active(true)
                .build();

        when(serviceRepository.findAll()).thenReturn(Arrays.asList(service, service2));

        List<ServiceDTO> result = serviceService.getAllServices();

        assertThat(result).hasSize(2);
        verify(serviceRepository).findAll();
    }

    @Test
    @DisplayName("getActiveServices - Retourne uniquement les services actifs")
    void getActiveServices_ReturnsOnlyActiveServices() {
        when(serviceRepository.findByActiveTrue()).thenReturn(Arrays.asList(service));

        List<ServiceDTO> result = serviceService.getActiveServices();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getActive()).isTrue();
    }

    @Test
    @DisplayName("getServiceById - Retourne le service quand il existe")
    void getServiceById_WhenExists_ReturnsService() {
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(service));

        ServiceDTO result = serviceService.getServiceById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Consultation standard");
    }

    @Test
    @DisplayName("getServiceById - Lance exception quand service n'existe pas")
    void getServiceById_WhenNotFound_ThrowsException() {
        when(serviceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> serviceService.getServiceById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("createService - Crée un nouveau service")
    void createService_WithValidData_ReturnsCreatedService() {
        ServiceDTO newServiceDTO = ServiceDTO.builder()
                .name("Nouveau service")
                .durationMinutes(45)
                .pointsAwarded(15)
                .active(true)
                .build();

        Service savedService = Service.builder()
                .id(2L)
                .name("Nouveau service")
                .durationMinutes(45)
                .pointsAwarded(15)
                .active(true)
                .build();

        when(serviceRepository.save(any(Service.class))).thenReturn(savedService);

        ServiceDTO result = serviceService.createService(newServiceDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
        verify(serviceRepository).save(any(Service.class));
    }

    @Test
    @DisplayName("updateService - Met à jour le service")
    void updateService_WithValidData_ReturnsUpdatedService() {
        ServiceDTO updateDTO = ServiceDTO.builder()
                .name("Service modifié")
                .description("Nouvelle description")
                .durationMinutes(45)
                .pointsAwarded(20)
                .active(true)
                .build();

        when(serviceRepository.findById(1L)).thenReturn(Optional.of(service));
        when(serviceRepository.save(any(Service.class))).thenReturn(service);

        ServiceDTO result = serviceService.updateService(1L, updateDTO);

        assertThat(result).isNotNull();
        verify(serviceRepository).save(any(Service.class));
    }

    @Test
    @DisplayName("updateService - Lance exception si service n'existe pas")
    void updateService_WhenNotFound_ThrowsException() {
        when(serviceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> serviceService.updateService(999L, serviceDTO))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("deleteService - Supprime le service")
    void deleteService_WhenExists_DeletesService() {
        when(serviceRepository.existsById(1L)).thenReturn(true);
        doNothing().when(serviceRepository).deleteById(1L);

        serviceService.deleteService(1L);

        verify(serviceRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteService - Lance exception si service n'existe pas")
    void deleteService_WhenNotFound_ThrowsException() {
        when(serviceRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> serviceService.deleteService(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("getServiceEntityById - Retourne l'entité Service")
    void getServiceEntityById_WhenExists_ReturnsEntity() {
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(service));

        Service result = serviceService.getServiceEntityById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("toDTO - Convertit Entity en DTO")
    void toDTO_ConvertsCorrectly() {
        ServiceDTO result = serviceService.toDTO(service);

        assertThat(result.getId()).isEqualTo(service.getId());
        assertThat(result.getName()).isEqualTo(service.getName());
        assertThat(result.getDurationMinutes()).isEqualTo(service.getDurationMinutes());
        assertThat(result.getPointsAwarded()).isEqualTo(service.getPointsAwarded());
    }

    @Test
    @DisplayName("toEntity - Convertit DTO en Entity")
    void toEntity_ConvertsCorrectly() {
        Service result = serviceService.toEntity(serviceDTO);

        assertThat(result.getName()).isEqualTo(serviceDTO.getName());
        assertThat(result.getDurationMinutes()).isEqualTo(serviceDTO.getDurationMinutes());
    }

    @Test
    @DisplayName("toEntity - Gère les valeurs null avec défauts")
    void toEntity_WithNullValues_SetsDefaults() {
        ServiceDTO dtoWithNulls = ServiceDTO.builder()
                .name("Test")
                .build();

        Service result = serviceService.toEntity(dtoWithNulls);

        assertThat(result.getDurationMinutes()).isEqualTo(30);
        assertThat(result.getPointsAwarded()).isEqualTo(10);
        assertThat(result.getActive()).isTrue();
    }
}
