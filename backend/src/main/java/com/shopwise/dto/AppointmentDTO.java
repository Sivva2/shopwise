package com.shopwise.dto;

import com.shopwise.entity.AppointmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentDTO {

    private Long id;

    @NotNull(message = "Le client est obligatoire")
    private Long clientId;

    private String clientName;

    private String clientEmail;

    @NotNull(message = "Le service est obligatoire")
    private Long serviceId;

    private String serviceName;

    private Integer serviceDuration;

    private Integer servicePoints;

    @NotNull(message = "La date est obligatoire")
    private LocalDate appointmentDate;

    @NotNull(message = "L'heure est obligatoire")
    private LocalTime appointmentTime;

    private AppointmentStatus status;

    private String notes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
