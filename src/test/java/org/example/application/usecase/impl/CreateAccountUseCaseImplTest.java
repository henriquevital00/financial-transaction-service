package org.example.application.usecase.impl;

import org.example.application.port.out.AccountRepositoryPort;
import org.example.application.usecase.CreateAccountUseCase;
import org.example.domain.model.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests: CreateAccountUseCaseImpl")
class CreateAccountUseCaseImplTest {

    @Mock
    private AccountRepositoryPort accountRepositoryPort;


    @InjectMocks
    private CreateAccountUseCaseImpl createAccountUseCase;

    @Test
    @DisplayName("Should execute use case and return response successfully")
    void shouldExecuteSuccessfully() {

        UUID idempotencyKey = UUID.randomUUID();
        CreateAccountUseCase.CreateAccountCommand command = new CreateAccountUseCase.CreateAccountCommand("11144477735", idempotencyKey);

        UUID accountId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        doAnswer(invocation -> {
            Account account = invocation.getArgument(0);
            return new Account(accountId, account.getDocumentNumber(), account.getStatus(), LocalDateTime.now(), LocalDateTime.now(), idempotencyKey);
        }).when(accountRepositoryPort).save(any(Account.class));


        Account response = createAccountUseCase.execute(command);


        assertNotNull(response);
        assertEquals(accountId, response.getId());
        assertEquals("11144477735", response.getDocumentNumberValue());
        assertEquals(org.example.domain.enums.AccountStatus.ACTIVE, response.getStatus());

        verify(accountRepositoryPort, times(1)).save(any(Account.class));
    }

    @Test
    @DisplayName("Should bubble up exception when account already exists")
    void shouldThrowExceptionWhenAccountExists() {

        UUID idempotencyKey = UUID.randomUUID();
        CreateAccountUseCase.CreateAccountCommand command = new CreateAccountUseCase.CreateAccountCommand("11144477735", idempotencyKey);
        
        when(accountRepositoryPort.save(any(Account.class))).thenThrow(new org.example.domain.exception.AccountAlreadyExistsException("11144477735"));


        org.example.domain.exception.AccountAlreadyExistsException exception = org.junit.jupiter.api.Assertions.assertThrows(
            org.example.domain.exception.AccountAlreadyExistsException.class,
            () -> createAccountUseCase.execute(command)
        );

        assertEquals("Account with document number 11144477735 already exists.", exception.getMessage());
        verify(accountRepositoryPort, times(1)).save(any(Account.class));
    }
}
