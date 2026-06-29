package org.example.domain.exception;

public class AccountNotFoundException extends BusinessException {
    public AccountNotFoundException(java.util.UUID id) {
        super(String.format("Account not found with ID: %s", id));
    }
}
