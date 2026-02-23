package com.shopwise.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExceptionTest {

    // ========== RESOURCE NOT FOUND EXCEPTION TESTS ==========

    @Test
    @DisplayName("ResourceNotFoundException - Constructor avec message")
    void resourceNotFoundException_WithMessage() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Client non trouvé");
        
        assertThat(exception.getMessage()).isEqualTo("Client non trouvé");
    }

    @Test
    @DisplayName("ResourceNotFoundException - Constructor avec resourceName et id")
    void resourceNotFoundException_WithResourceNameAndId() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Client", 123L);
        
        assertThat(exception.getMessage()).isEqualTo("Client avec l'id 123 non trouvé");
    }

    // ========== BUSINESS EXCEPTION TESTS ==========

    @Test
    @DisplayName("BusinessException - Constructor avec message")
    void businessException_WithMessage() {
        BusinessException exception = new BusinessException("Email déjà utilisé");
        
        assertThat(exception.getMessage()).isEqualTo("Email déjà utilisé");
    }

    // ========== GLOBAL EXCEPTION HANDLER TESTS ==========

    @Test
    @DisplayName("GlobalExceptionHandler - handleResourceNotFoundException")
    void globalExceptionHandler_HandleResourceNotFoundException() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        ResourceNotFoundException exception = new ResourceNotFoundException("Client", 1L);
        
        ResponseEntity<Map<String, Object>> response = handler.handleResourceNotFoundException(exception);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).containsKey("timestamp");
        assertThat(response.getBody().get("status")).isEqualTo(404);
        assertThat(response.getBody().get("error")).isEqualTo("Not Found");
        assertThat(response.getBody().get("message")).isEqualTo("Client avec l'id 1 non trouvé");
    }

    @Test
    @DisplayName("GlobalExceptionHandler - handleBusinessException")
    void globalExceptionHandler_HandleBusinessException() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        BusinessException exception = new BusinessException("Opération non autorisée");
        
        ResponseEntity<Map<String, Object>> response = handler.handleBusinessException(exception);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsKey("timestamp");
        assertThat(response.getBody().get("status")).isEqualTo(400);
        assertThat(response.getBody().get("error")).isEqualTo("Bad Request");
        assertThat(response.getBody().get("message")).isEqualTo("Opération non autorisée");
    }

    @Test
    @DisplayName("GlobalExceptionHandler - handleValidationExceptions")
    void globalExceptionHandler_HandleValidationExceptions() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        
        FieldError fieldError1 = new FieldError("client", "email", "Email invalide");
        FieldError fieldError2 = new FieldError("client", "firstName", "Prénom obligatoire");
        
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError1, fieldError2));
        
        ResponseEntity<Map<String, Object>> response = handler.handleValidationExceptions(exception);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsKey("timestamp");
        assertThat(response.getBody().get("status")).isEqualTo(400);
        assertThat(response.getBody().get("error")).isEqualTo("Validation Error");
        assertThat(response.getBody()).containsKey("errors");
        
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) response.getBody().get("errors");
        assertThat(errors).containsEntry("email", "Email invalide");
        assertThat(errors).containsEntry("firstName", "Prénom obligatoire");
    }

    @Test
    @DisplayName("GlobalExceptionHandler - handleGenericException")
    void globalExceptionHandler_HandleGenericException() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        Exception exception = new RuntimeException("Erreur inattendue");
        
        ResponseEntity<Map<String, Object>> response = handler.handleGenericException(exception);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).containsKey("timestamp");
        assertThat(response.getBody().get("status")).isEqualTo(500);
        assertThat(response.getBody().get("error")).isEqualTo("Internal Server Error");
        assertThat(response.getBody().get("message")).isEqualTo("Une erreur inattendue s'est produite");
    }
}
