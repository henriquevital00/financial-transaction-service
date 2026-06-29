package org.example.infrastructure.adapter.out.persistence;


import lombok.RequiredArgsConstructor;
import org.example.application.port.out.AccountRepositoryPort;
import org.example.domain.exception.AccountAlreadyExistsException;
import org.example.domain.model.Account;
import org.example.infrastructure.adapter.out.persistence.converter.AccountMapper;
import org.example.infrastructure.adapter.out.persistence.entity.AccountEntity;
import org.example.infrastructure.adapter.out.persistence.jpa.AccountJpaRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AccountRepositoryAdapter implements AccountRepositoryPort {

    private final AccountJpaRepository jpaRepository;
    private final AccountMapper converter;

    @Override
    public Account save(Account account) {
        try {
            AccountEntity entity = converter.toEntity(account);
            entity.setCreatedAt(account.getCreatedAt());
            entity.setUpdatedAt(account.getUpdatedAt());
            entity.setIdempotencyKey(account.getIdempotencyKey());

            AccountEntity saved = jpaRepository.saveAndFlush(entity);
            return converter.toDomain(saved);
        } catch (DataIntegrityViolationException ex) {
            throw new AccountAlreadyExistsException(account.getDocumentNumberValue());
        }
    }

    @Override
    public Optional<Account> findByDocumentNumber(String documentNumber) {
        return jpaRepository.findByDocumentNumber(documentNumber)
                .map(converter::toDomain);
    }

    @Override
    public Optional<Account> findByIdempotencyKey(java.util.UUID idempotencyKey) {
        return jpaRepository.findByIdempotencyKey(idempotencyKey)
                .map(converter::toDomain);
    }

    @Override
    public Optional<Account> findById(java.util.UUID id) {
        return jpaRepository.findById(id)
                .map(converter::toDomain);
    }
}

