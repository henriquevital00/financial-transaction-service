package org.example.application.usecase;

import org.example.domain.model.Transaction;

import java.math.BigDecimal;
import java.util.UUID;

public interface CreateTransactionUseCase {
    Transaction execute(CreateTransactionCommand command);

    record CreateTransactionCommand(
            UUID accountId,
            int operationTypeId,
            BigDecimal amount,
            UUID idempotencyKey
    ) {}
}
