package com.shopwise.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceDTO {

    private Long id;

    @NotBlank(message = "Le nom du service est obligatoire")
    @Size(max = 150, message = "Le nom ne peut pas dépasser 150 caractères")
    private String name;

    private String description;

    @Min(value = 1, message = "La durée doit être d'au moins 1 minute")
    private Integer durationMinutes;

    @Min(value = 0, message = "Les points attribués ne peuvent pas être négatifs")
    private Integer pointsAwarded;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
