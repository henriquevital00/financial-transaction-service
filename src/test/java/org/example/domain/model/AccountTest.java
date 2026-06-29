package org.example.domain.model;

import org.example.domain.enums.AccountStatus;
import org.example.domain.value.DocumentNumber;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Tests: Account Entity")
class AccountTest {

    @Test
    @DisplayName("Should create new account")
    void shouldCreateNewAccount() {
        DocumentNumber doc = new DocumentNumber("11144477735");
        Account account = new Account("11144477735", java.util.UUID.randomUUID());

        assertNull(account.getId());
        assertEquals(doc.getValue(), account.getDocumentNumberValue());
        assertEquals(AccountStatus.ACTIVE, account.getStatus());
        assertNotNull(account.getCreatedAt());
        assertNotNull(account.getUpdatedAt());
    }

    @Test
    @DisplayName("Should restore existing account")
    void shouldRestoreExistingAccount() {
        DocumentNumber doc = new DocumentNumber("11144477735");
        LocalDateTime now = LocalDateTime.now();
        UUID accountId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        Account account = new Account(accountId, doc, AccountStatus.ACTIVE, now, now, java.util.UUID.randomUUID());

        assertEquals(accountId, account.getId());
        assertEquals(doc, account.getDocumentNumber());
        assertEquals(AccountStatus.ACTIVE, account.getStatus());
        assertEquals(now, account.getCreatedAt());
        assertEquals(now, account.getUpdatedAt());
    }
}
