package org.example.infrastructure.adapter.out.persistence.converter;

import org.example.domain.model.OperationType;
import org.example.domain.model.Transaction;
import org.example.domain.value.TransactionAmount;
import org.example.infrastructure.adapter.out.persistence.entity.TransactionEntity;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public Transaction toDomain(TransactionEntity entity) {
        if (entity == null) return null;
        
        return Transaction.builder()
                .id(entity.getId())
                .accountId(entity.getAccountId())
                .operationType(OperationType.fromId(entity.getOperationTypeId()))
                .amount(new TransactionAmount(entity.getAmount().abs(), OperationType.fromId(entity.getOperationTypeId())))
                .idempotencyKey(entity.getIdempotencyKey())
                .eventDate(entity.getEventDate())
                .build();
    }

    public TransactionEntity toEntity(Transaction domain) {
        if (domain == null) return null;

        return TransactionEntity.builder()
                .id(domain.getId())
                .accountId(domain.getAccountId())
                .operationTypeId(domain.getOperationType().getId())
                .amount(domain.getAmount().getValue())
                .idempotencyKey(domain.getIdempotencyKey())
                .eventDate(domain.getEventDate())
                .build();
    }
}
