package org.example.domain.model;

import lombok.Builder;
import lombok.Data;
import org.example.domain.value.TransactionAmount;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class Transaction {
    private UUID id;
    private UUID accountId;
    private OperationType operationType;
    private TransactionAmount amount;
    private UUID idempotencyKey;
    private LocalDateTime eventDate;

    public Transaction(UUID id, UUID accountId, OperationType operationType, TransactionAmount amount, UUID idempotencyKey, LocalDateTime eventDate) {
        this.id = id;
        this.accountId = accountId;
        this.operationType = operationType;
        this.amount = amount;
        this.idempotencyKey = idempotencyKey;
        this.eventDate = eventDate != null ? eventDate : LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.MICROS);
    }

    public Transaction(UUID accountId, int operationTypeId, BigDecimal rawAmount, UUID idempotencyKey) {
        this.accountId = accountId;
        this.operationType = OperationType.fromId(operationTypeId);
        this.amount = new TransactionAmount(rawAmount, this.operationType);
        this.idempotencyKey = idempotencyKey;
        this.eventDate = LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.MICROS);
    }
}
