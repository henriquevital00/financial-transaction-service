package org.example.application.usecase.impl;

import org.example.application.port.out.AccountRepositoryPort;
import org.example.domain.exception.AccountNotFoundException;
import org.example.domain.model.Account;
import org.example.domain.value.DocumentNumber;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAccountUseCaseImplTest {

    @Mock
    private AccountRepositoryPort accountRepositoryPort;

    @InjectMocks
    private GetAccountUseCaseImpl getAccountUseCase;

    @Test
    void getAccount_ShouldReturnAccount_WhenFound() {
        UUID accountId = UUID.randomUUID();
        Account expectedAccount = Account.builder()
                .id(accountId)
                .documentNumber(new DocumentNumber("11144477735"))
                .build();

        when(accountRepositoryPort.findById(accountId)).thenReturn(Optional.of(expectedAccount));

        Account actualAccount = getAccountUseCase.getAccount(accountId);

        assertNotNull(actualAccount);
        assertEquals(accountId, actualAccount.getId());
        verify(accountRepositoryPort).findById(accountId);
    }

    @Test
    void getAccount_ShouldThrowAccountNotFoundException_WhenNotFound() {
        UUID accountId = UUID.randomUUID();
        when(accountRepositoryPort.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> getAccountUseCase.getAccount(accountId));
        verify(accountRepositoryPort).findById(accountId);
    }
}
