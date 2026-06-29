package org.example.infrastructure.adapter.out.persistence.converter;

import org.example.domain.enums.AccountStatus;
import org.example.domain.model.Account;
import org.example.domain.value.DocumentNumber;
import org.example.infrastructure.adapter.out.persistence.entity.AccountEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Unit Tests: AccountMapper")
class AccountMapperTest {

    @Test
    @DisplayName("Should convert Account to AccountJpaEntity")
    void shouldConvertAccountToEntity() {
        LocalDateTime now = LocalDateTime.now();
        UUID accountId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        Account account = new Account(accountId, new DocumentNumber("11144477735"), AccountStatus.ACTIVE, now, now, java.util.UUID.randomUUID());

        AccountMapper converter = new AccountMapper();
        AccountEntity entity = converter.toEntity(account);

        assertEquals(accountId, entity.getId());
        assertEquals("11144477735", entity.getDocumentNumber());
        assertEquals(AccountStatus.ACTIVE, entity.getStatus());
        assertEquals(now, entity.getCreatedAt());
        assertEquals(now, entity.getUpdatedAt());
    }

    @Test
    @DisplayName("Should convert AccountJpaEntity to Account")
    void shouldConvertEntityToDomain() {
        LocalDateTime now = LocalDateTime.now();
        UUID accountId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        AccountEntity entity = new AccountEntity(accountId, "11144477735", AccountStatus.ACTIVE, now, now, java.util.UUID.randomUUID());

        AccountMapper converter = new AccountMapper();
        Account account = converter.toDomain(entity);

        assertEquals(accountId, account.getId());
        assertEquals("11144477735", account.getDocumentNumber().getValue());
        assertEquals(AccountStatus.ACTIVE, account.getStatus());
        assertEquals(now, account.getCreatedAt());
        assertEquals(now, account.getUpdatedAt());
    }
}
