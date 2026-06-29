package org.example.infrastructure.adapter.out.persistence;

import org.example.domain.enums.AccountStatus;
import org.example.domain.model.Account;
import org.example.domain.value.DocumentNumber;
import org.example.infrastructure.adapter.out.persistence.converter.AccountMapper;
import org.example.infrastructure.adapter.out.persistence.entity.AccountEntity;
import org.example.infrastructure.adapter.out.persistence.jpa.AccountJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests: AccountRepositoryAdapter")
class AccountRepositoryAdapterTest {

    @Mock
    private AccountJpaRepository accountJpaRepository;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountRepositoryAdapter accountRepositoryAdapter;

    @Test
    @DisplayName("Should save account successfully")
    void shouldSaveAccount() {
        LocalDateTime now = LocalDateTime.now();
        UUID accountId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        Account account = new Account(accountId, new DocumentNumber("11144477735"), AccountStatus.ACTIVE, now, now, java.util.UUID.randomUUID());

        AccountEntity savedEntity = new AccountEntity(accountId, "11144477735", AccountStatus.ACTIVE, now, now, java.util.UUID.randomUUID());

        when(accountMapper.toEntity(any(Account.class))).thenReturn(savedEntity);
        when(accountJpaRepository.saveAndFlush(any(AccountEntity.class))).thenReturn(savedEntity);
        when(accountMapper.toDomain(any(AccountEntity.class))).thenReturn(account);

        Account result = accountRepositoryAdapter.save(account);

        assertNotNull(result);
        assertEquals(accountId, result.getId());
        assertEquals("11144477735", result.getDocumentNumber().getValue());

        verify(accountJpaRepository, times(1)).saveAndFlush(any(AccountEntity.class));
    }

    @Test
    @DisplayName("Should find account by document number")
    void shouldFindAccountByDocumentNumber() {
        LocalDateTime now = LocalDateTime.now();
        UUID accountId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        AccountEntity savedEntity = new AccountEntity(accountId, "11144477735", AccountStatus.ACTIVE, now, now, java.util.UUID.randomUUID());

        Account mappedAccount = new Account(accountId, new DocumentNumber("11144477735"), AccountStatus.ACTIVE, now, now, java.util.UUID.randomUUID());

        when(accountJpaRepository.findByDocumentNumber("11144477735")).thenReturn(java.util.Optional.of(savedEntity));
        when(accountMapper.toDomain(any(AccountEntity.class))).thenReturn(mappedAccount);

        java.util.Optional<Account> account = accountRepositoryAdapter.findByDocumentNumber("11144477735");

        assertTrue(account.isPresent());
        verify(accountJpaRepository, times(1)).findByDocumentNumber("11144477735");
    }
}
