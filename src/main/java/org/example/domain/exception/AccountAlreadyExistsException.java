package org.example.domain.exception;

public class AccountAlreadyExistsException extends BusinessException {
    public AccountAlreadyExistsException(String documentNumber) {
        super("Account with document number " + documentNumber + " already exists.");
    }
}