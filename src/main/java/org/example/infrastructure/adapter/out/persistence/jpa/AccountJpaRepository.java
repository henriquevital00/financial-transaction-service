package org.example.infrastructure.adapter.out.persistence.jpa;

import org.example.infrastructure.adapter.out.persistence.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import java.util.UUID;

@Repository
public interface AccountJpaRepository extends JpaRepository<AccountEntity, UUID> {
    Optional<AccountEntity> findByDocumentNumber(String documentNumber);
    Optional<AccountEntity> findByIdempotencyKey(UUID idempotencyKey);
}

