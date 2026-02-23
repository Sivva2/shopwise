package com.shopwise.controller;

import com.shopwise.dto.PointTransactionDTO;
import com.shopwise.entity.TransactionType;
import com.shopwise.service.PointTransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PointTransactionController.class)
class PointTransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PointTransactionService transactionService;

    private PointTransactionDTO transactionDTO;

    @BeforeEach
    void setUp() {
        transactionDTO = PointTransactionDTO.builder()
                .id(1L)
                .clientId(1L)
                .clientName("Marie Dupont")
                .points(10)
                .transactionType(TransactionType.EARNED)
                .description("Points gagnés")
                .build();
    }

    @Test
    @DisplayName("GET /api/loyalty/client/{clientId}/transactions - Retourne les transactions")
    void getClientTransactions_ReturnsTransactions() throws Exception {
        when(transactionService.getTransactionsByClient(1L)).thenReturn(Arrays.asList(transactionDTO));

        mockMvc.perform(get("/api/loyalty/client/1/transactions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].points", is(10)));

        verify(transactionService).getTransactionsByClient(1L);
    }

    @Test
    @DisplayName("GET /api/loyalty/client/{clientId}/balance - Retourne le solde")
    void getClientBalance_ReturnsBalance() throws Exception {
        when(transactionService.getClientBalance(1L)).thenReturn(100);
        when(transactionService.getTotalEarnedPoints(1L)).thenReturn(150);
        when(transactionService.getTotalRedeemedPoints(1L)).thenReturn(50);

        mockMvc.perform(get("/api/loyalty/client/1/balance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId", is(1)))
                .andExpect(jsonPath("$.balance", is(100)))
                .andExpect(jsonPath("$.totalEarned", is(150)))
                .andExpect(jsonPath("$.totalRedeemed", is(50)));
    }

    @Test
    @DisplayName("GET /api/loyalty/transactions/{id} - Retourne la transaction")
    void getTransactionById_ReturnsTransaction() throws Exception {
        when(transactionService.getTransactionById(1L)).thenReturn(transactionDTO);

        mockMvc.perform(get("/api/loyalty/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.transactionType", is("EARNED")));
    }

    @Test
    @DisplayName("POST /api/loyalty/client/{clientId}/redeem - Utilise des points")
    void redeemPoints_ReturnsTransaction() throws Exception {
        PointTransactionDTO redeemDTO = PointTransactionDTO.builder()
                .id(2L)
                .clientId(1L)
                .points(-50)
                .transactionType(TransactionType.REDEEMED)
                .description("Réduction appliquée")
                .build();

        when(transactionService.redeemPoints(1L, 50, "Réduction appliquée")).thenReturn(redeemDTO);

        mockMvc.perform(post("/api/loyalty/client/1/redeem")
                        .param("points", "50")
                        .param("description", "Réduction appliquée"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.points", is(-50)))
                .andExpect(jsonPath("$.transactionType", is("REDEEMED")));
    }

    @Test
    @DisplayName("POST /api/loyalty/client/{clientId}/redeem - Sans description")
    void redeemPoints_WithoutDescription_ReturnsTransaction() throws Exception {
        PointTransactionDTO redeemDTO = PointTransactionDTO.builder()
                .id(2L)
                .clientId(1L)
                .points(-50)
                .transactionType(TransactionType.REDEEMED)
                .build();

        when(transactionService.redeemPoints(1L, 50, null)).thenReturn(redeemDTO);

        mockMvc.perform(post("/api/loyalty/client/1/redeem")
                        .param("points", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.points", is(-50)));
    }

    @Test
    @DisplayName("POST /api/loyalty/client/{clientId}/adjust - Ajuste les points")
    void adjustPoints_ReturnsTransaction() throws Exception {
        PointTransactionDTO adjustDTO = PointTransactionDTO.builder()
                .id(3L)
                .clientId(1L)
                .points(25)
                .transactionType(TransactionType.ADJUSTMENT)
                .description("Bonus promotionnel")
                .build();

        when(transactionService.adjustPoints(1L, 25, "Bonus promotionnel")).thenReturn(adjustDTO);

        mockMvc.perform(post("/api/loyalty/client/1/adjust")
                        .param("points", "25")
                        .param("description", "Bonus promotionnel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.points", is(25)))
                .andExpect(jsonPath("$.transactionType", is("ADJUSTMENT")));
    }

    @Test
    @DisplayName("POST /api/loyalty/client/{clientId}/adjust - Sans description")
    void adjustPoints_WithoutDescription_ReturnsTransaction() throws Exception {
        PointTransactionDTO adjustDTO = PointTransactionDTO.builder()
                .id(3L)
                .clientId(1L)
                .points(-10)
                .transactionType(TransactionType.ADJUSTMENT)
                .build();

        when(transactionService.adjustPoints(1L, -10, null)).thenReturn(adjustDTO);

        mockMvc.perform(post("/api/loyalty/client/1/adjust")
                        .param("points", "-10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.points", is(-10)));
    }
}
