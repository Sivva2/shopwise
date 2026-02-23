package com.shopwise.dto;

import com.shopwise.entity.TransactionType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointTransactionDTO {

    private Long id;

    @NotNull(message = "Le client est obligatoire")
    private Long clientId;

    private String clientName;

    private Long appointmentId;

    @NotNull(message = "Le nombre de points est obligatoire")
    private Integer points;

    @NotNull(message = "Le type de transaction est obligatoire")
    private TransactionType transactionType;

    private String description;

    private LocalDateTime createdAt;
}
