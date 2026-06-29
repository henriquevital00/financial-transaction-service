package org.example.domain.exception;

public class TransactionAlreadyExistsException extends BusinessException {
    public TransactionAlreadyExistsException() {
        super("Transaction already processed (Idempotency Key collision)");
    }
}
