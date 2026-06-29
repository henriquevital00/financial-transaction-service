package org.example.infrastructure.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.example.application.port.in.GetAccountUseCase;
import org.example.application.usecase.CreateAccountUseCase;
import org.example.domain.model.Account;
import org.example.generated.api.AccountsApi;
import org.example.generated.model.AccountResponse;
import org.example.generated.model.CreateAccountRequest;
import org.example.infrastructure.adapter.in.web.mapper.AccountResponseMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AccountController implements AccountsApi {

    private final CreateAccountUseCase createAccountUseCase;
    private final GetAccountUseCase getAccountUseCase;
    private final AccountResponseMapper accountResponseMapper;

    @Override
    public ResponseEntity<AccountResponse> createAccount(UUID idempotencyKey, CreateAccountRequest createAccountRequest) {
        CreateAccountUseCase.CreateAccountCommand command = new CreateAccountUseCase.CreateAccountCommand(
                createAccountRequest.getDocumentNumber(),
                idempotencyKey
        );

        Account account = createAccountUseCase.execute(command);
        AccountResponse response = accountResponseMapper.toResponse(account);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<AccountResponse> getAccount(UUID accountId) {
        Account account = getAccountUseCase.getAccount(accountId);
        AccountResponse response = accountResponseMapper.toResponse(account);
        return ResponseEntity.ok(response);
    }
}
