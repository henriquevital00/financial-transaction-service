package org.example.infrastructure.adapter.in.web.exception;

import org.example.domain.exception.AccountAlreadyExistsException;
import org.example.domain.exception.BusinessException;
import org.example.generated.model.ErrorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Unit Tests: GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Should handle BusinessException and return 400")
    void shouldHandleBusinessException() {
        BusinessException exception = new BusinessException("Invalid CPF");
        
        ResponseEntity<ErrorResponse> response = handler.handleBusinessException(exception);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("400", response.getBody().getCode());
        assertEquals("Business rule violation", response.getBody().getMessage());
        assertEquals(ErrorResponse.LevelEnum.WARNING, response.getBody().getLevel());
        assertEquals("Invalid CPF", response.getBody().getDescription());
    }

    @Test
    @DisplayName("Should handle AccountAlreadyExistsException and return 409")
    void shouldHandleAccountAlreadyExistsException() {
        AccountAlreadyExistsException exception = new AccountAlreadyExistsException("11144477735");
        
        ResponseEntity<ErrorResponse> response = handler.handleAccountAlreadyExists(exception);
        
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("409", response.getBody().getCode());
        assertEquals("Account already exists", response.getBody().getMessage());
        assertEquals(ErrorResponse.LevelEnum.ERROR, response.getBody().getLevel());
        assertEquals("Account with document number 11144477735 already exists.", response.getBody().getDescription());
    }
    @Test
    @DisplayName("Should handle MissingRequestHeaderException and return 400")
    void shouldHandleMissingRequestHeaderException() {
        org.springframework.core.MethodParameter parameter = mock(org.springframework.core.MethodParameter.class);
        when(parameter.getNestedParameterType()).thenReturn((Class) String.class);
        org.springframework.web.bind.MissingRequestHeaderException exception = 
            new org.springframework.web.bind.MissingRequestHeaderException("Idempotency-Key", parameter);
        
        ResponseEntity<ErrorResponse> response = handler.handleMissingRequestHeaderException(exception);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("400", response.getBody().getCode());
        assertEquals("Missing required header", response.getBody().getMessage());
        assertEquals(ErrorResponse.LevelEnum.WARNING, response.getBody().getLevel());
        assertEquals(exception.getMessage(), response.getBody().getDescription());
    }

    @Test
    @DisplayName("Should handle MethodArgumentTypeMismatchException and return 400")
    void shouldHandleMethodArgumentTypeMismatchException() {
        org.springframework.core.MethodParameter parameter = mock(org.springframework.core.MethodParameter.class);
        when(parameter.getNestedParameterType()).thenReturn((Class) String.class);
        org.springframework.web.method.annotation.MethodArgumentTypeMismatchException exception = 
            new org.springframework.web.method.annotation.MethodArgumentTypeMismatchException("invalid-uuid", java.util.UUID.class, "Idempotency-Key", parameter, new IllegalArgumentException("Invalid UUID string: invalid-uuid"));
        
        ResponseEntity<ErrorResponse> response = handler.handleTypeMismatchException(exception);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("400", response.getBody().getCode());
        assertEquals("Invalid parameter type", response.getBody().getMessage());
        assertEquals(ErrorResponse.LevelEnum.WARNING, response.getBody().getLevel());
        assertEquals("Parameter 'Idempotency-Key' should be of type UUID", response.getBody().getDescription());
    }
}
