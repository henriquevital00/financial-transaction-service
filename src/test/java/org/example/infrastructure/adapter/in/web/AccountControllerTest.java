package org.example.infrastructure.adapter.in.web;

import org.example.application.port.in.GetAccountUseCase;
import org.example.application.usecase.CreateAccountUseCase;
import org.example.domain.model.Account;
import org.example.generated.model.AccountResponse;
import org.example.generated.model.CreateAccountRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests: AccountController")
class AccountControllerTest {

    @Mock
    private CreateAccountUseCase createAccountUseCase;

    @Mock
    private org.example.infrastructure.adapter.in.web.mapper.AccountResponseMapper accountResponseMapper;

    @Mock
    private GetAccountUseCase getAccountUseCase;

    @InjectMocks
    private AccountController accountController;

    private UUID idempotencyKey;
    private CreateAccountRequest request;
    private AccountResponse mockResponse;

    @BeforeEach
    void setUp() {
        idempotencyKey = UUID.randomUUID();
        
        request = new CreateAccountRequest();
        request.setDocumentNumber("11144477735");

        mockResponse = new AccountResponse();
        mockResponse.setId(UUID.randomUUID());
    }

    @Test
    @DisplayName("Should create account and return 201 Created")
    void shouldCreateAccountSuccessfully() {
        Account mockAccount = new Account("11144477735", idempotencyKey);
        
        when(createAccountUseCase.execute(any(CreateAccountUseCase.CreateAccountCommand.class))).thenReturn(mockAccount);
        when(accountResponseMapper.toResponse(any(Account.class))).thenReturn(mockResponse);

        ResponseEntity<AccountResponse> response = accountController.createAccount(idempotencyKey, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());

        verify(createAccountUseCase, times(1)).execute(any(CreateAccountUseCase.CreateAccountCommand.class));
        verify(accountResponseMapper, times(1)).toResponse(any(Account.class));
    }

    @Test
    @DisplayName("Should get account and return 200 OK")
    void shouldGetAccountSuccessfully() {
        UUID accountId = UUID.randomUUID();
        Account mockAccount = new Account("11144477735", idempotencyKey);
        
        when(getAccountUseCase.getAccount(accountId)).thenReturn(mockAccount);
        when(accountResponseMapper.toResponse(mockAccount)).thenReturn(mockResponse);

        ResponseEntity<AccountResponse> response = accountController.getAccount(accountId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());

        verify(getAccountUseCase, times(1)).getAccount(accountId);
        verify(accountResponseMapper, times(1)).toResponse(mockAccount);
    }
}
