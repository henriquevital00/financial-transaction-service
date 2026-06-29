package org.example.application.usecase.impl;

import org.example.application.port.out.TransactionRepositoryPort;
import org.example.application.usecase.CreateTransactionUseCase;
import org.example.domain.exception.AccountNotFoundException;
import org.example.domain.model.Transaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateTransactionUseCaseImplTest {

    @Mock
    private TransactionRepositoryPort transactionRepositoryPort;

    @Mock
    private org.example.application.port.in.GetAccountUseCase getAccountUseCase;

    @InjectMocks
    private CreateTransactionUseCaseImpl createTransactionUseCase;

    @Test
    void shouldCreateTransactionSuccessfully() {

        UUID accountId = UUID.randomUUID();
        int operationTypeId = 1;
        BigDecimal amount = BigDecimal.valueOf(50.0);
        UUID idempotencyKey = UUID.randomUUID();

        CreateTransactionUseCase.CreateTransactionCommand command = new CreateTransactionUseCase.CreateTransactionCommand(
                accountId, operationTypeId, amount, idempotencyKey
        );

        org.example.domain.model.Account mockAccount = org.example.domain.model.Account.builder()
                .id(accountId)
                .status(org.example.domain.enums.AccountStatus.ACTIVE)
                .build();
        when(getAccountUseCase.getAccount(accountId)).thenReturn(mockAccount);

        when(transactionRepositoryPort.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction t = invocation.getArgument(0);
            t.setId(UUID.randomUUID());
            return t;
        });


        Transaction result = createTransactionUseCase.execute(command);


        assertNotNull(result.getId());
        assertEquals(accountId, result.getAccountId());
        assertEquals(-50.0, result.getAmount().getValue().doubleValue());
        
        verify(transactionRepositoryPort).save(any(Transaction.class));
    }

    @Test
    void shouldThrowExceptionWhenAccountDoesNotExist() {

        UUID accountId = UUID.randomUUID();
        CreateTransactionUseCase.CreateTransactionCommand command = new CreateTransactionUseCase.CreateTransactionCommand(
                accountId, 1, BigDecimal.valueOf(50.0), UUID.randomUUID()
        );

        when(getAccountUseCase.getAccount(accountId)).thenThrow(new AccountNotFoundException(accountId));



        assertThrows(AccountNotFoundException.class, () -> createTransactionUseCase.execute(command));
    }

    @Test
    void shouldThrowExceptionWhenAccountIsNotActive() {

        UUID accountId = UUID.randomUUID();
        CreateTransactionUseCase.CreateTransactionCommand command = new CreateTransactionUseCase.CreateTransactionCommand(
                accountId, 1, BigDecimal.valueOf(50.0), UUID.randomUUID()
        );

        org.example.domain.model.Account mockAccount = org.example.domain.model.Account.builder()
                .id(accountId)
                .status(org.example.domain.enums.AccountStatus.BLOCKED)
                .build();
        when(getAccountUseCase.getAccount(accountId)).thenReturn(mockAccount);


        org.example.domain.exception.BusinessException exception = assertThrows(
                org.example.domain.exception.BusinessException.class, 
                () -> createTransactionUseCase.execute(command)
        );
        assertEquals("Cannot process transaction: Account is not ACTIVE", exception.getMessage());
    }
}
