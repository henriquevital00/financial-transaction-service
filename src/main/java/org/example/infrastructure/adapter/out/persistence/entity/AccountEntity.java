package org.example.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.domain.enums.AccountStatus;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "accounts", indexes = {
        @Index(name = "idx_document_number", columnList = "document_number", unique = true),
        @Index(name = "idx_created_at", columnList = "created_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "document_number", nullable = false, unique = true, length = 14)
    private String documentNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AccountStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "idempotency_key", unique = true)
    private UUID idempotencyKey;

}
