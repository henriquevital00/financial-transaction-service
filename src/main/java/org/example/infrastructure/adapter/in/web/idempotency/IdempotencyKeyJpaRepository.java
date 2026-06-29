package org.example.infrastructure.adapter.in.web.idempotency;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface IdempotencyKeyJpaRepository extends JpaRepository<IdempotencyKeyEntity, String> {

    @Modifying
    @Query("UPDATE IdempotencyKeyEntity i SET i.lockedAt = :newLockedAt, i.requestPath = :path, i.responseStatus = null, i.responseBody = null WHERE i.idempotencyKey = :key AND i.lockedAt = :oldLockedAt")
    int takeOverLock(String key, LocalDateTime oldLockedAt, LocalDateTime newLockedAt, String path);
}
