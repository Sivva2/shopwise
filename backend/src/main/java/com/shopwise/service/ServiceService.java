package com.shopwise.service;

import com.shopwise.dto.ServiceDTO;
import com.shopwise.entity.Service;
import com.shopwise.exception.ResourceNotFoundException;
import com.shopwise.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Transactional
public class ServiceService {

    private final ServiceRepository serviceRepository;

    public List<ServiceDTO> getAllServices() {
        return serviceRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ServiceDTO> getActiveServices() {
        return serviceRepository.findByActiveTrue().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ServiceDTO getServiceById(Long id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service", id));
        return toDTO(service);
    }

    public ServiceDTO createService(ServiceDTO serviceDTO) {
        Service service = toEntity(serviceDTO);
        service = serviceRepository.save(service);
        return toDTO(service);
    }

    public ServiceDTO updateService(Long id, ServiceDTO serviceDTO) {
        Service existingService = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service", id));

        existingService.setName(serviceDTO.getName());
        existingService.setDescription(serviceDTO.getDescription());
        existingService.setDurationMinutes(serviceDTO.getDurationMinutes());
        existingService.setPointsAwarded(serviceDTO.getPointsAwarded());
        existingService.setActive(serviceDTO.getActive());

        existingService = serviceRepository.save(existingService);
        return toDTO(existingService);
    }

    public void deleteService(Long id) {
        if (!serviceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Service", id);
        }
        serviceRepository.deleteById(id);
    }

    public Service getServiceEntityById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service", id));
    }

    // Mapper Entity -> DTO
    public ServiceDTO toDTO(Service service) {
        return ServiceDTO.builder()
                .id(service.getId())
                .name(service.getName())
                .description(service.getDescription())
                .durationMinutes(service.getDurationMinutes())
                .pointsAwarded(service.getPointsAwarded())
                .active(service.getActive())
                .createdAt(service.getCreatedAt())
                .updatedAt(service.getUpdatedAt())
                .build();
    }

    // Mapper DTO -> Entity
    public Service toEntity(ServiceDTO dto) {
        return Service.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .durationMinutes(dto.getDurationMinutes() != null ? dto.getDurationMinutes() : 30)
                .pointsAwarded(dto.getPointsAwarded() != null ? dto.getPointsAwarded() : 10)
                .active(dto.getActive() != null ? dto.getActive() : true)
                .build();
    }
}
