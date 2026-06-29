package org.example.infrastructure.adapter.in.web.idempotency;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final IdempotencyKeyJpaRepository repository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void tryLock(String key, String path) {
        IdempotencyKeyEntity entity = new IdempotencyKeyEntity();
        entity.setIdempotencyKey(key);
        entity.setRequestPath(path);
        entity.setLockedAt(LocalDateTime.now());
        
        repository.saveAndFlush(entity);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public IdempotencyKeyEntity getByKey(String key) {
        return repository.findById(key)
                .orElseThrow(() -> new IllegalStateException("Key vanished"));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveResponse(String key, int status, String body) {
        repository.findById(key).ifPresent(entity -> {
            entity.setResponseStatus(status);
            entity.setResponseBody(body);
            entity.setLockedAt(null);
            repository.save(entity);
        });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean tryTakeOverLock(String key, LocalDateTime oldLockedAt, String path) {
        int updatedRows = repository.takeOverLock(key, oldLockedAt, LocalDateTime.now(), path);
        return updatedRows > 0;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteKey(String key) {
        repository.deleteById(key);
    }
}
