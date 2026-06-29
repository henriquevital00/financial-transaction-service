package org.example.infrastructure.adapter.in.web;

import org.example.generated.model.CreateAccountRequest;
import org.example.generated.model.CreateTransactionRequest;
import org.example.infrastructure.AbstractIntegrationTest;
import org.example.infrastructure.adapter.out.persistence.jpa.AccountJpaRepository;
import org.example.infrastructure.adapter.out.persistence.jpa.TransactionJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Integration Tests: Transaction Management")
class TransactionIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TransactionJpaRepository transactionRepository;

    @Autowired
    private AccountJpaRepository accountRepository;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAllInBatch();
        accountRepository.deleteAllInBatch();
    }

    private String createAccountAndGetId() throws Exception {
        CreateAccountRequest request = new CreateAccountRequest();
        request.setDocumentNumber("12345678901234");
        
        String responseStr = mockMvc.perform(post("/v1/accounts")
                .header("Idempotency-Key", UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();
                
        return objectMapper.readTree(responseStr).get("id").asText();
    }

    @Test
    @DisplayName("Should create transaction successfully for Purchase (Negative)")
    void shouldCreateTransactionSuccessfully() throws Exception {
        String accountId = createAccountAndGetId();

        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setAccountId(UUID.fromString(accountId));
        request.setOperationTypeId(1);
        request.setAmount(100.50);

        mockMvc.perform(post("/v1/transactions")
                .header("Idempotency-Key", UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transaction_id").exists())
                .andExpect(jsonPath("$.account_id").value(accountId))
                .andExpect(jsonPath("$.operation_type_id").value(1))
                .andExpect(jsonPath("$.amount").value(-100.50))
                .andExpect(jsonPath("$.event_date").exists());
    }

    @Test
    @DisplayName("Should create transaction successfully for Credit (Positive)")
    void shouldCreateCreditTransactionSuccessfully() throws Exception {
        String accountId = createAccountAndGetId();

        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setAccountId(UUID.fromString(accountId));
        request.setOperationTypeId(4);
        request.setAmount(50.0);

        mockMvc.perform(post("/v1/transactions")
                .header("Idempotency-Key", UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(50.0));
    }

    @Test
    @DisplayName("Should return 404 Not Found if Account does not exist")
    void shouldReturnNotFoundIfAccountDoesNotExist() throws Exception {
        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setAccountId(UUID.randomUUID());
        request.setOperationTypeId(1);
        request.setAmount(100.0);

        mockMvc.perform(post("/v1/transactions")
                .header("Idempotency-Key", UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("404"));
    }

    @Test
    @DisplayName("Should return exactly the same response when called twice with the same Idempotency-Key")
    void shouldReturnIdempotentResponseWhenCalledTwiceWithSameKey() throws Exception {
        String accountId = createAccountAndGetId();

        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setAccountId(UUID.fromString(accountId));
        request.setOperationTypeId(1);
        request.setAmount(100.0);
        
        String idempotencyKey = UUID.randomUUID().toString();

        String firstResponse = mockMvc.perform(post("/v1/transactions")
                .header("Idempotency-Key", idempotencyKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String secondResponse = mockMvc.perform(post("/v1/transactions")
                .header("Idempotency-Key", idempotencyKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        org.junit.jupiter.api.Assertions.assertEquals(firstResponse, secondResponse);
    }
    
    @Test
    @DisplayName("Should return 400 Bad Request if invalid operation type")
    void shouldReturnBadRequestIfInvalidOperationType() throws Exception {
        String accountId = createAccountAndGetId();

        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setAccountId(UUID.fromString(accountId));
        request.setOperationTypeId(999);
        request.setAmount(100.0);

        mockMvc.perform(post("/v1/transactions")
                .header("Idempotency-Key", UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("Invalid Operation Type"));
    }
}
