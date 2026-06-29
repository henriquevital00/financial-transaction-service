package org.example.infrastructure.adapter.in.web.mapper;

import org.example.domain.model.Transaction;
import org.example.generated.model.TransactionResponse;
import org.springframework.stereotype.Component;

@Component
public class TransactionResponseMapper {

    public TransactionResponse toResponse(Transaction domain) {
        if (domain == null) return null;

        TransactionResponse response = new TransactionResponse();
        response.setTransactionId(domain.getId());
        response.setAccountId(domain.getAccountId());
        response.setOperationTypeId(domain.getOperationType().getId());
        response.setAmount(domain.getAmount().getValue().doubleValue());
        
        if (domain.getEventDate() != null) {
            response.setEventDate(domain.getEventDate().atZone(java.time.ZoneId.systemDefault()).toOffsetDateTime());
        }
        
        return response;
    }
}
