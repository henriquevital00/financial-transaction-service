package org.example.infrastructure.adapter.in.web;

import org.example.generated.model.CreateAccountRequest;
import org.example.infrastructure.AbstractIntegrationTest;
import org.example.infrastructure.adapter.out.persistence.jpa.AccountJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Integration Tests: Account Management")
class AccountIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private AccountJpaRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAllInBatch();
    }

    @Test
    @DisplayName("Should create account successfully and return 201 Created")
    void shouldCreateAccountSuccessfully() throws Exception {
        CreateAccountRequest request = new CreateAccountRequest();
        request.setDocumentNumber("12345678901234");

        mockMvc.perform(post("/v1/accounts")
                .header("Idempotency-Key", java.util.UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.documentNumber").value("12345678901234"))
                .andExpect(jsonPath("$.documentNumberFormatted").value("12.345.678/9012-34"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when document number is empty")
    void shouldReturnBadRequestWhenDocumentNumberIsEmpty() throws Exception {
        CreateAccountRequest request = new CreateAccountRequest();
        request.setDocumentNumber("");

        mockMvc.perform(post("/v1/accounts")
                .header("Idempotency-Key", java.util.UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.level").value("warning"))
                .andExpect(jsonPath("$.description").exists());
    }

    @Test
    @DisplayName("Should return 409 Conflict when account already exists")
    void shouldReturnConflictWhenAccountAlreadyExists() throws Exception {
        CreateAccountRequest request = new CreateAccountRequest();
        request.setDocumentNumber("98765432109876");

        mockMvc.perform(post("/v1/accounts")
                .header("Idempotency-Key", java.util.UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/v1/accounts")
                .header("Idempotency-Key", java.util.UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("409"))
                .andExpect(jsonPath("$.message").value("Account already exists"))
                .andExpect(jsonPath("$.level").value("error"))
                .andExpect(jsonPath("$.description").exists());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when document number has invalid format (letters/wrong size)")
    void shouldReturnBadRequestWhenDocumentNumberHasInvalidFormat() throws Exception {
        CreateAccountRequest request = new CreateAccountRequest();
        request.setDocumentNumber("111ABC77735");

        mockMvc.perform(post("/v1/accounts")
                .header("Idempotency-Key", java.util.UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.level").value("warning"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when document number is mathematically invalid (Business Rule)")
    void shouldReturnBadRequestWhenDocumentNumberIsMathematicallyInvalid() throws Exception {
        CreateAccountRequest request = new CreateAccountRequest();
        request.setDocumentNumber("12345678900");

        mockMvc.perform(post("/v1/accounts")
                .header("Idempotency-Key", java.util.UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("Business rule violation"))
                .andExpect(jsonPath("$.level").value("warning"))
                .andExpect(jsonPath("$.description").value("Invalid CPF format"));
    }

    @Test
    @DisplayName("Should return exactly the same response when called twice with the same Idempotency-Key")
    void shouldReturnIdempotentResponseWhenCalledTwiceWithSameKey() throws Exception {
        CreateAccountRequest request = new CreateAccountRequest();
        request.setDocumentNumber("12345678901234");
        String idempotencyKey = java.util.UUID.randomUUID().toString();

        String firstResponse = mockMvc.perform(post("/v1/accounts")
                .header("Idempotency-Key", idempotencyKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.documentNumber").value("12345678901234"))
                .andReturn().getResponse().getContentAsString();

        String secondResponse = mockMvc.perform(post("/v1/accounts")
                .header("Idempotency-Key", idempotencyKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        org.junit.jupiter.api.Assertions.assertEquals(firstResponse, secondResponse);
    }

    @Test
    @DisplayName("Should return 400 Bad Request when Idempotency-Key header is missing")
    void shouldReturnBadRequestWhenIdempotencyKeyIsMissing() throws Exception {
        CreateAccountRequest request = new CreateAccountRequest();
        request.setDocumentNumber("12345678901234");

        mockMvc.perform(post("/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("Missing required header"))
                .andExpect(jsonPath("$.level").value("warning"))
                .andExpect(jsonPath("$.description").exists());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when Idempotency-Key header is an invalid UUID")
    void shouldReturnBadRequestWhenIdempotencyKeyIsInvalidUuid() throws Exception {
        CreateAccountRequest request = new CreateAccountRequest();
        request.setDocumentNumber("12345678901234");

        mockMvc.perform(post("/v1/accounts")
                .header("Idempotency-Key", "{{uuid}}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("Invalid parameter type"))
                .andExpect(jsonPath("$.level").value("warning"))
                .andExpect(jsonPath("$.description").value("Parameter 'Idempotency-Key' should be of type UUID"));
    }

    @Test
    @DisplayName("Should get account successfully and return 200 OK")
    void shouldGetAccountSuccessfully() throws Exception {
        CreateAccountRequest request = new CreateAccountRequest();
        request.setDocumentNumber("12345678901234");
        String idempotencyKey = java.util.UUID.randomUUID().toString();

        String responseStr = mockMvc.perform(post("/v1/accounts")
                .header("Idempotency-Key", idempotencyKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
                
        String accountId = objectMapper.readTree(responseStr).get("id").asText();

        mockMvc.perform(get("/v1/accounts/" + accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(accountId))
                .andExpect(jsonPath("$.documentNumber").value("12345678901234"))
                .andExpect(jsonPath("$.documentNumberFormatted").value("12.345.678/9012-34"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @DisplayName("Should return 404 Not Found when account does not exist")
    void shouldReturnNotFoundWhenAccountDoesNotExist() throws Exception {
        String randomAccountId = java.util.UUID.randomUUID().toString();

        mockMvc.perform(get("/v1/accounts/" + randomAccountId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("Account not found"))
                .andExpect(jsonPath("$.level").value("warning"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when account ID is not a valid UUID")
    void shouldReturnBadRequestWhenAccountIdIsInvalid() throws Exception {
        String invalidAccountId = "not-a-valid-uuid";

        mockMvc.perform(get("/v1/accounts/" + invalidAccountId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("Invalid parameter type"))
                .andExpect(jsonPath("$.level").value("warning"))
                .andExpect(jsonPath("$.description").value("Parameter 'accountId' should be of type UUID"));
    }
}
