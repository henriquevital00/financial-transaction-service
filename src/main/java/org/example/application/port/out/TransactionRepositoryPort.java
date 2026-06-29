package org.example.application.port.out;

import org.example.domain.model.Transaction;

import java.util.Optional;
import java.util.UUID;

public interface TransactionRepositoryPort {
    Transaction save(Transaction transaction);
    Optional<Transaction> findByIdempotencyKey(UUID idempotencyKey);
}
