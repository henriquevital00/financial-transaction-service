package org.example.application.port.in;

import org.example.domain.model.Account;
import java.util.UUID;

public interface GetAccountUseCase {
    Account getAccount(UUID accountId);
}
