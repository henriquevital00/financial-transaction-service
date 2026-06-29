package org.example.infrastructure.adapter.in.web.mapper;

import org.example.domain.model.Account;
import org.example.generated.model.AccountResponse;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

/**
 * Mapper para converter entidades de domínio em DTOs da aplicação.
 */
@Component
public class AccountResponseMapper {

    public AccountResponse toResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getDocumentNumberValue(),
                account.getDocumentNumber().getFormatted(),
                account.getStatus() != null ? AccountResponse.StatusEnum.fromValue(account.getStatus().name()) : null,
                account.getCreatedAt() != null ? account.getCreatedAt().atOffset(ZoneOffset.UTC) : null,
                account.getUpdatedAt() != null ? account.getUpdatedAt().atOffset(ZoneOffset.UTC) : null
        );
    }
}
