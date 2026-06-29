package org.example.application.port.out;

import org.example.domain.model.Account;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepositoryPort {
    Account save(Account account);

    Optional<Account> findByDocumentNumber(String documentNumber);
    Optional<Account> findByIdempotencyKey(UUID idempotencyKey);
    Optional<Account> findById(UUID id);
}