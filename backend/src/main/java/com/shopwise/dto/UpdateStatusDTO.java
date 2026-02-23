package com.shopwise.dto;

import com.shopwise.entity.AppointmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusDTO {

    @NotNull(message = "Le statut est obligatoire")
    private AppointmentStatus status;
}
