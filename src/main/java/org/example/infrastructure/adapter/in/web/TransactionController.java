package org.example.infrastructure.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.example.application.usecase.CreateTransactionUseCase;
import org.example.domain.model.Transaction;
import org.example.generated.api.TransactionsApi;
import org.example.generated.model.CreateTransactionRequest;
import org.example.generated.model.TransactionResponse;
import org.example.infrastructure.adapter.in.web.mapper.TransactionResponseMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TransactionController implements TransactionsApi {

    private final CreateTransactionUseCase createTransactionUseCase;
    private final TransactionResponseMapper transactionResponseMapper;

    @Override
    public ResponseEntity<TransactionResponse> createTransaction(UUID idempotencyKey, CreateTransactionRequest request) {
        CreateTransactionUseCase.CreateTransactionCommand command = new CreateTransactionUseCase.CreateTransactionCommand(
                request.getAccountId(),
                request.getOperationTypeId(),
                BigDecimal.valueOf(request.getAmount()),
                idempotencyKey
        );

        Transaction transaction = createTransactionUseCase.execute(command);
        TransactionResponse response = transactionResponseMapper.toResponse(transaction);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
