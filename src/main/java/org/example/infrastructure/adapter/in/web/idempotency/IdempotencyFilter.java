package org.example.infrastructure.adapter.in.web.idempotency;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class IdempotencyFilter extends OncePerRequestFilter {

    private final IdempotencyService idempotencyService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String idempotencyKey = request.getHeader("Idempotency-Key");

        if (idempotencyKey == null || request.getMethod().equalsIgnoreCase("GET")) {
            filterChain.doFilter(request, response);
            return;
        }

        boolean acquired = false;
        try {
            idempotencyService.tryLock(idempotencyKey, request.getRequestURI());
            acquired = true;
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            IdempotencyKeyEntity existing = idempotencyService.getByKey(idempotencyKey);

            if (existing.getLockedAt() != null) {
                if (existing.getLockedAt().isBefore(java.time.LocalDateTime.now().minusMinutes(2))) {
                    acquired = idempotencyService.tryTakeOverLock(idempotencyKey, existing.getLockedAt(), request.getRequestURI());
                }
                
                if (!acquired) {
                    response.setStatus(HttpStatus.CONFLICT.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.getWriter().write("{\"code\": \"409\", \"message\": \"Concurrent request\", \"level\": \"error\", \"description\": \"This idempotency key is currently being processed.\"}");
                    return;
                }
            } else {
                response.setStatus(existing.getResponseStatus());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write(existing.getResponseBody());
                return;
            }
        }

        if (acquired) {
            proceedWithProcessing(request, response, filterChain, idempotencyKey);
        }
    }

    private void proceedWithProcessing(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, String idempotencyKey) throws IOException, ServletException {
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        boolean errorOccurred = false;
        try {
            filterChain.doFilter(request, responseWrapper);
        } catch (Exception ex) {
            errorOccurred = true;
            throw ex;
        } finally {
            int status = responseWrapper.getStatus();
            if (errorOccurred || status >= 500) {
                idempotencyService.deleteKey(idempotencyKey);
            } else {
                byte[] responseArray = responseWrapper.getContentAsByteArray();
                String responseBody = new String(responseArray, StandardCharsets.UTF_8);
                idempotencyService.saveResponse(idempotencyKey, status, responseBody);
            }
            responseWrapper.copyBodyToResponse();
        }
    }
}
