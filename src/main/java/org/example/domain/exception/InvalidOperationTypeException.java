package org.example.domain.exception;

public class InvalidOperationTypeException extends BusinessException {
    public InvalidOperationTypeException(int id) {
        super(String.format("Invalid operation type ID: %d", id));
    }
}
