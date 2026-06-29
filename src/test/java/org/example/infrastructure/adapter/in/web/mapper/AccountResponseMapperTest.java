package org.example.infrastructure.adapter.in.web.mapper;

import org.example.domain.enums.AccountStatus;
import org.example.domain.model.Account;
import org.example.domain.value.DocumentNumber;
import org.example.generated.model.AccountResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Unit Tests: AccountResponseMapper")
class AccountResponseMapperTest {

    @Test
    @DisplayName("Should map Account to AccountResponse")
    void shouldMapAccountToResponse() {
        LocalDateTime now = LocalDateTime.now();
        UUID accountId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        Account account = new Account(accountId, new DocumentNumber("11144477735"), AccountStatus.ACTIVE, now, now, java.util.UUID.randomUUID());

        AccountResponseMapper mapper = new AccountResponseMapper();
        AccountResponse response = mapper.toResponse(account);

        assertEquals(accountId, response.getId());
        assertEquals("11144477735", response.getDocumentNumber());
        assertEquals("111.444.777-35", response.getDocumentNumberFormatted());
        assertEquals(AccountResponse.StatusEnum.ACTIVE, response.getStatus());
    }
}
