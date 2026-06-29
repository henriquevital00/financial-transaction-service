package org.example.application.usecase;

import org.example.domain.model.Account;

import java.util.UUID;

public interface CreateAccountUseCase {

    Account execute(CreateAccountCommand command);

    record CreateAccountCommand(String documentNumber, UUID idempotencyKey) {}
}