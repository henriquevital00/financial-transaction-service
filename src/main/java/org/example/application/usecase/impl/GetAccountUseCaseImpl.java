package org.example.application.usecase.impl;

import lombok.RequiredArgsConstructor;
import org.example.application.port.in.GetAccountUseCase;
import org.example.application.port.out.AccountRepositoryPort;
import org.example.domain.exception.AccountNotFoundException;
import org.example.domain.model.Account;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetAccountUseCaseImpl implements GetAccountUseCase {

    private final AccountRepositoryPort accountRepositoryPort;

    @Override
    public Account getAccount(UUID accountId) {
        return accountRepositoryPort.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }
}
