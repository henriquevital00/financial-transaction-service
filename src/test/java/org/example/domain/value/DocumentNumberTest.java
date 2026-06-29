package org.example.domain.value;

import org.example.domain.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Tests: DocumentNumber Value Object")
class DocumentNumberTest {

    @Test
    @DisplayName("Should create valid CPF")
    void shouldCreateValidCPF() {
        DocumentNumber doc = new DocumentNumber("11144477735");
        assertEquals("11144477735", doc.getValue());
        assertEquals("111.444.777-35", doc.getFormatted());
    }

    @Test
    @DisplayName("Should create valid CNPJ")
    void shouldCreateValidCNPJ() {
        DocumentNumber doc = new DocumentNumber("12345678901234");
        assertEquals("12345678901234", doc.getValue());
        assertEquals("12.345.678/9012-34", doc.getFormatted());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "123"})
    @DisplayName("Should throw exception for invalid document length or empty")
    void shouldThrowExceptionForInvalidLengthOrEmpty(String invalidDoc) {
        BusinessException exception = assertThrows(BusinessException.class, () -> new DocumentNumber(invalidDoc));
        assertNotNull(exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for invalid CPF")
    void shouldThrowExceptionForInvalidCPF() {
        BusinessException exception = assertThrows(BusinessException.class, () -> new DocumentNumber("12345678900"));
        assertEquals("Invalid CPF format", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for formatted characters")
    void shouldThrowExceptionForFormattedCharacters() {
        BusinessException exception = assertThrows(BusinessException.class, () -> new DocumentNumber("111.444.777-35"));
        assertEquals("Document number must contain exactly 11 (CPF) or 14 (CNPJ) digits", exception.getMessage());
    }
}
