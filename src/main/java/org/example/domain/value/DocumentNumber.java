package org.example.domain.value;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.example.domain.exception.BusinessException;

@Getter
@EqualsAndHashCode
public final class DocumentNumber {
    private final String value;

    public DocumentNumber(String value) {
        this.value = validateAndNormalize(value);
    }

    private String validateAndNormalize(String value) {
        if (value == null || value.isBlank()) {
            throw new BusinessException("Document number cannot be null or blank");
        }

        if (!value.matches("^(\\d{11}|\\d{14})$")) {
            throw new BusinessException("Document number must contain exactly 11 (CPF) or 14 (CNPJ) digits");
        }

        if (value.length() == 11 && !isValidCPF(value)) {
            throw new BusinessException("Invalid CPF format");
        }

        if (value.length() == 14 && !isValidCNPJ(value)) {
            throw new BusinessException("Invalid CNPJ format");
        }

        return value;
    }

    private boolean isValidCPF(String cpf) {
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        int sum = 0;
        int remainder;

        for (int i = 1; i <= 9; i++) {
            sum += Character.getNumericValue(cpf.charAt(i - 1)) * (11 - i);
        }

        remainder = (sum * 10) % 11;
        if (remainder == 10 || remainder == 11) {
            remainder = 0;
        }

        if (remainder != Character.getNumericValue(cpf.charAt(9))) {
            return false;
        }

        sum = 0;
        for (int i = 1; i <= 10; i++) {
            sum += Character.getNumericValue(cpf.charAt(i - 1)) * (12 - i);
        }

        remainder = (sum * 10) % 11;
        if (remainder == 10 || remainder == 11) {
            remainder = 0;
        }

        return remainder == Character.getNumericValue(cpf.charAt(10));
    }

    private boolean isValidCNPJ(String cnpj) {
        if (cnpj.matches("(\\d)\\1{13}")) {
            return false;
        }

        return true;
    }

    public String getFormatted() {
        if (value.length() == 11) {
            return String.format("%s.%s.%s-%s",
                    value.substring(0, 3),
                    value.substring(3, 6),
                    value.substring(6, 9),
                    value.substring(9));
        } else {
            return String.format("%s.%s.%s/%s-%s",
                    value.substring(0, 2),
                    value.substring(2, 5),
                    value.substring(5, 8),
                    value.substring(8, 12),
                    value.substring(12));
        }
    }

    @Override
    public String toString() {
        return getFormatted();
    }
}


