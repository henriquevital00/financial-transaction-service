package org.example.infrastructure.adapter.out.persistence.converter;

import org.example.domain.model.Account;
import org.example.domain.value.DocumentNumber;
import org.example.infrastructure.adapter.out.persistence.entity.AccountEntity;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public AccountEntity toEntity(Account account) {
        return new AccountEntity(
                account.getId(),
                account.getDocumentNumberValue(),
                account.getStatus(),
                account.getCreatedAt(),
                account.getUpdatedAt(),
                account.getIdempotencyKey()
        );
    }

    public Account toDomain(AccountEntity entity) {
        return new Account(
                entity.getId(),
                new DocumentNumber(entity.getDocumentNumber()),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getIdempotencyKey()
        );
    }
}

