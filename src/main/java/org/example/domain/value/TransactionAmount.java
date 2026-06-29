package org.example.domain.value;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.example.domain.exception.BusinessException;
import org.example.domain.model.OperationType;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@EqualsAndHashCode
public class TransactionAmount {
    private final BigDecimal value;

    public TransactionAmount(BigDecimal rawAmount, OperationType operationType) {
        if (rawAmount == null || rawAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Amount must be greater than zero");
        }

        BigDecimal scaledAmount = rawAmount.setScale(2, RoundingMode.HALF_UP);
        
        if (operationType.isNegative()) {
            this.value = scaledAmount.negate();
        } else {
            this.value = scaledAmount;
        }
    }
}
