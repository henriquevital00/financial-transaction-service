package org.example.infrastructure.adapter.in.web.idempotency;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IdempotencyFilterTest {

    @Mock
    private IdempotencyService idempotencyService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private IdempotencyFilter idempotencyFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setRequestURI("/v1/accounts");
        response = new MockHttpServletResponse();
    }

    @Test
    @DisplayName("Should bypass GET requests")
    void shouldBypassGetRequests() throws ServletException, IOException {
        request.setMethod("GET");
        
        idempotencyFilter.doFilterInternal(request, response, filterChain);
        
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(idempotencyService);
    }

    @Test
    @DisplayName("Should bypass if no Idempotency-Key header is present")
    void shouldBypassIfNoHeader() throws ServletException, IOException {
        idempotencyFilter.doFilterInternal(request, response, filterChain);
        
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(idempotencyService);
    }

    @Test
    @DisplayName("Should proceed and save response if lock is acquired successfully")
    void shouldProceedIfLockAcquired() throws ServletException, IOException {
        String key = "test-key";
        request.addHeader("Idempotency-Key", key);


        doNothing().when(idempotencyService).tryLock(eq(key), any());

        doAnswer(invocation -> {
            ContentCachingResponseWrapper res = invocation.getArgument(1);
            res.setStatus(201);
            res.getWriter().write("{\"success\":true}");
            return null;
        }).when(filterChain).doFilter(any(), any());

        idempotencyFilter.doFilterInternal(request, response, filterChain);

        verify(idempotencyService).tryLock(key, "/v1/accounts");
        verify(filterChain).doFilter(any(), any());
        verify(idempotencyService).saveResponse(eq(key), eq(201), eq("{\"success\":true}"));
        assertEquals(201, response.getStatus());
        assertEquals("{\"success\":true}", response.getContentAsString());
    }

    @Test
    @DisplayName("Should delete key if an unhandled exception occurs (500)")
    void shouldDeleteKeyIfUnhandledException() throws ServletException, IOException {
        String key = "test-key";
        request.addHeader("Idempotency-Key", key);

        doNothing().when(idempotencyService).tryLock(eq(key), any());

        doThrow(new RuntimeException("Simulated error")).when(filterChain).doFilter(any(), any());

        try {
            idempotencyFilter.doFilterInternal(request, response, filterChain);
        } catch (RuntimeException e) {

        }

        verify(idempotencyService).deleteKey(key);
        verify(idempotencyService, never()).saveResponse(any(), anyInt(), any());
    }
    
    @Test
    @DisplayName("Should delete key if status is 5xx")
    void shouldDeleteKeyIfStatusIs5xx() throws ServletException, IOException {
        String key = "test-key";
        request.addHeader("Idempotency-Key", key);

        doNothing().when(idempotencyService).tryLock(eq(key), any());

        doAnswer(invocation -> {
            ContentCachingResponseWrapper res = invocation.getArgument(1);
            res.setStatus(500);
            return null;
        }).when(filterChain).doFilter(any(), any());

        idempotencyFilter.doFilterInternal(request, response, filterChain);

        verify(idempotencyService).deleteKey(key);
        verify(idempotencyService, never()).saveResponse(any(), anyInt(), any());
    }

    @Test
    @DisplayName("Should return cached response if already processed")
    void shouldReturnCachedResponseIfAlreadyProcessed() throws ServletException, IOException {
        String key = "test-key";
        request.addHeader("Idempotency-Key", key);

        doThrow(new DataIntegrityViolationException("Duplicate")).when(idempotencyService).tryLock(key, "/v1/accounts");

        IdempotencyKeyEntity existing = new IdempotencyKeyEntity();
        existing.setIdempotencyKey(key);
        existing.setResponseStatus(201);
        existing.setResponseBody("{\"cached\":true}");

        
        when(idempotencyService.getByKey(key)).thenReturn(existing);

        idempotencyFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, never()).doFilter(any(), any());
        assertEquals(201, response.getStatus());
        assertEquals("{\"cached\":true}", response.getContentAsString());
    }

    @Test
    @DisplayName("Should return 409 Conflict if lockedAt is not null and not expired")
    void shouldReturnConflictIfInProgress() throws ServletException, IOException {
        String key = "test-key";
        request.addHeader("Idempotency-Key", key);

        doThrow(new DataIntegrityViolationException("Duplicate")).when(idempotencyService).tryLock(key, "/v1/accounts");

        IdempotencyKeyEntity existing = new IdempotencyKeyEntity();
        existing.setIdempotencyKey(key);
        existing.setLockedAt(LocalDateTime.now());
        
        when(idempotencyService.getByKey(key)).thenReturn(existing);

        idempotencyFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, never()).doFilter(any(), any());
        assertEquals(409, response.getStatus());
    }
}
