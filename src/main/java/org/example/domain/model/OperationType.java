package org.example.domain.model;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum OperationType {
    NORMAL_PURCHASE(1, true),
    PURCHASE_WITH_INSTALLMENTS(2, true),
    WITHDRAWAL(3, true),
    CREDIT_VOUCHER(4, false);

    private final int id;
    private final boolean negative;

    OperationType(int id, boolean negative) {
        this.id = id;
        this.negative = negative;
    }

    public static OperationType fromId(int id) {
        return Arrays.stream(values())
                .filter(type -> type.getId() == id)
                .findFirst()
                .orElseThrow(() -> new org.example.domain.exception.InvalidOperationTypeException(id));
    }
}
