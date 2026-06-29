package org.example.infrastructure.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.example.application.port.out.TransactionRepositoryPort;
import org.example.domain.exception.TransactionAlreadyExistsException;
import org.example.domain.model.Transaction;
import org.example.infrastructure.adapter.out.persistence.converter.TransactionMapper;
import org.example.infrastructure.adapter.out.persistence.entity.TransactionEntity;
import org.example.infrastructure.adapter.out.persistence.jpa.TransactionJpaRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TransactionRepositoryAdapter implements TransactionRepositoryPort {

    private final TransactionJpaRepository jpaRepository;
    private final TransactionMapper converter;

    @Override
    public Transaction save(Transaction transaction) {
        try {
            TransactionEntity entity = converter.toEntity(transaction);
            TransactionEntity saved = jpaRepository.saveAndFlush(entity);
            return converter.toDomain(saved);
        } catch (DataIntegrityViolationException ex) {
            throw new TransactionAlreadyExistsException();
        }
    }

    @Override
    public Optional<Transaction> findByIdempotencyKey(UUID idempotencyKey) {
        return jpaRepository.findByIdempotencyKey(idempotencyKey)
                .map(converter::toDomain);
    }
}
