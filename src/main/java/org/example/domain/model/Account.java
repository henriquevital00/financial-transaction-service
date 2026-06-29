package org.example.domain.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.example.domain.enums.AccountStatus;
import org.example.domain.value.DocumentNumber;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class Account {
    private UUID id;
    private DocumentNumber documentNumber;
    private AccountStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID idempotencyKey;

    public Account(String documentNumberValue, UUID idempotencyKey) {
        this.documentNumber = new DocumentNumber(documentNumberValue);
        this.status = AccountStatus.ACTIVE;
        this.idempotencyKey = idempotencyKey;
        this.createdAt = LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.MICROS);
        this.updatedAt = this.createdAt;
    }

    public boolean isActive() {
        return this.status == AccountStatus.ACTIVE;
    }

    public String getDocumentNumberValue() {
        return documentNumber != null ? documentNumber.getValue() : null;
    }
}