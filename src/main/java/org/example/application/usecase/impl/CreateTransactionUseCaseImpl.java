package org.example.application.usecase.impl;

import lombok.RequiredArgsConstructor;
import org.example.application.port.in.GetAccountUseCase;
import org.example.application.port.out.TransactionRepositoryPort;
import org.example.application.usecase.CreateTransactionUseCase;
import org.example.domain.exception.BusinessException;
import org.example.domain.model.Account;
import org.example.domain.model.Transaction;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateTransactionUseCaseImpl implements CreateTransactionUseCase {

    private final TransactionRepositoryPort transactionRepositoryPort;
    private final GetAccountUseCase getAccountUseCase;

    @Override
    public Transaction execute(CreateTransactionCommand command) {
        
        Account account = getAccountUseCase.getAccount(command.accountId());
        
        if (!account.isActive()) {
            throw new BusinessException("Cannot process transaction: Account is not ACTIVE");
        }

        Transaction transaction = new Transaction(
                command.accountId(),
                command.operationTypeId(),
                command.amount(),
                command.idempotencyKey()
        );

        return transactionRepositoryPort.save(transaction);
    }
}
