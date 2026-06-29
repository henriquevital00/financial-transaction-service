package org.example.domain.enums;

/**
 * Estados possíveis de uma conta.
 */
public enum AccountStatus {
    ACTIVE("Conta ativa"),
    INACTIVE("Conta inativa"),
    BLOCKED("Conta bloqueada");

    private final String description;

    AccountStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

