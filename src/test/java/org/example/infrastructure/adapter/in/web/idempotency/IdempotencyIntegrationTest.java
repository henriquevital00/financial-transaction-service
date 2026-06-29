package org.example.infrastructure.adapter.in.web.idempotency;

import org.example.application.usecase.CreateAccountUseCase;
import org.example.generated.model.CreateAccountRequest;
import org.example.infrastructure.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Integration Tests: Idempotency")
class IdempotencyIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private IdempotencyKeyJpaRepository idempotencyRepository;

    @SpyBean
    private CreateAccountUseCase createAccountUseCase;

    @BeforeEach
    void setUp() {
        idempotencyRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("Should return exactly the same response (201) when called twice")
    void shouldReturnCachedResponseOnSubsequentRequests() throws Exception {
        CreateAccountRequest request = new CreateAccountRequest();
        request.setDocumentNumber("12345678901234");
        String idempotencyKey = UUID.randomUUID().toString();

        String firstResponse = mockMvc.perform(post("/v1/accounts")
                .header("Idempotency-Key", idempotencyKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String secondResponse = mockMvc.perform(post("/v1/accounts")
                .header("Idempotency-Key", idempotencyKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        org.junit.jupiter.api.Assertions.assertEquals(firstResponse, secondResponse);
        
        assertTrue(idempotencyRepository.findById(idempotencyKey).isPresent());
        var entity = idempotencyRepository.findById(idempotencyKey).get();
        org.junit.jupiter.api.Assertions.assertNull(entity.getLockedAt());
        org.junit.jupiter.api.Assertions.assertEquals(201, entity.getResponseStatus());
    }

    @Test
    @DisplayName("Should cache 400 Bad Request responses")
    void shouldCacheBadRequest() throws Exception {
        CreateAccountRequest request = new CreateAccountRequest();
        request.setDocumentNumber("invalid");
        String idempotencyKey = UUID.randomUUID().toString();

        mockMvc.perform(post("/v1/accounts")
                .header("Idempotency-Key", idempotencyKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        assertTrue(idempotencyRepository.findById(idempotencyKey).isPresent());
        var entity = idempotencyRepository.findById(idempotencyKey).get();
        org.junit.jupiter.api.Assertions.assertEquals(400, entity.getResponseStatus());

        mockMvc.perform(post("/v1/accounts")
                .header("Idempotency-Key", idempotencyKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should delete idempotency key on 500 error to allow retry")
    void shouldDeleteIdempotencyKeyOn500Error() throws Exception {
        CreateAccountRequest request = new CreateAccountRequest();
        request.setDocumentNumber("12345678901234");
        String idempotencyKey = UUID.randomUUID().toString();

        doThrow(new RuntimeException("Simulated Internal Server Error")).when(createAccountUseCase).execute(any());

        mockMvc.perform(post("/v1/accounts")
                .header("Idempotency-Key", idempotencyKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());

        assertFalse(idempotencyRepository.findById(idempotencyKey).isPresent());
    }

    @Test
    @DisplayName("Should return 409 Conflict if request is in progress")
    void shouldReturn409ConflictIfInProgress() throws Exception {
        String idempotencyKey = UUID.randomUUID().toString();
        
        IdempotencyKeyEntity entity = new IdempotencyKeyEntity();
        entity.setIdempotencyKey(idempotencyKey);
        entity.setRequestPath("/v1/accounts");
        entity.setLockedAt(LocalDateTime.now());
        idempotencyRepository.saveAndFlush(entity);

        CreateAccountRequest request = new CreateAccountRequest();
        request.setDocumentNumber("12345678901234");

        mockMvc.perform(post("/v1/accounts")
                .header("Idempotency-Key", idempotencyKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Concurrent request"));
    }

    @Test
    @DisplayName("Should take over lock if it is expired (older than 2 minutes)")
    void shouldTakeOverLockIfExpired() throws Exception {
        String idempotencyKey = UUID.randomUUID().toString();
        
        IdempotencyKeyEntity entity = new IdempotencyKeyEntity();
        entity.setIdempotencyKey(idempotencyKey);
        entity.setRequestPath("/v1/accounts");
        entity.setLockedAt(LocalDateTime.now().minusMinutes(3));
        idempotencyRepository.saveAndFlush(entity);

        CreateAccountRequest request = new CreateAccountRequest();
        request.setDocumentNumber("98765432109876");

        mockMvc.perform(post("/v1/accounts")
                .header("Idempotency-Key", idempotencyKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        var updatedEntity = idempotencyRepository.findById(idempotencyKey).get();
        org.junit.jupiter.api.Assertions.assertNull(updatedEntity.getLockedAt());
        org.junit.jupiter.api.Assertions.assertEquals(201, updatedEntity.getResponseStatus());
    }
}
