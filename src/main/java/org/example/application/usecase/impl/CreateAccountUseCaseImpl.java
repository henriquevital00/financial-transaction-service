package org.example.application.usecase.impl;


import lombok.RequiredArgsConstructor;
import org.example.application.port.out.AccountRepositoryPort;
import org.example.application.usecase.CreateAccountUseCase;
import org.example.domain.model.Account;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateAccountUseCaseImpl implements CreateAccountUseCase {

    private final AccountRepositoryPort accountRepositoryPort;

    @Override
    public Account execute(CreateAccountCommand command) {
        Account account = new Account(command.documentNumber(), command.idempotencyKey());
        return accountRepositoryPort.save(account);
    }
}