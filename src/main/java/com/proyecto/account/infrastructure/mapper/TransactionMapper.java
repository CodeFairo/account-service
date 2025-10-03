package com.proyecto.account.infrastructure.mapper;

import com.proyecto.account.domain.model.Transaction;
import com.proyecto.account.model.TransactionDTO;
import com.proyecto.account.model.TransactionRequestDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Component
public class TransactionMapper {

    public Transaction toDomain(String accountId, String type, TransactionRequestDTO dto, BigDecimal balanceAfter) {
        return Transaction.builder()
                .accountId(accountId)
                .type(type)
                .amount(BigDecimal.valueOf(dto.getAmount()))
                .concept(dto.getConcept())
                .balanceAfter(balanceAfter)
                .dateTransaction(LocalDateTime.now())
                .build();
    }

    public TransactionDTO toDto(Transaction tx) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(tx.getId());
        dto.setType(TransactionDTO.TypeEnum.valueOf(tx.getType()));
        dto.setAmount(tx.getAmount().doubleValue());
        dto.setBalanceAfter(tx.getBalanceAfter().doubleValue());
        dto.setTimestamp(
                Optional.ofNullable(tx.getDateTransaction())
                        .map(date -> date.atOffset(ZoneOffset.UTC))
                        .orElse(null)
        );

        return dto;
    }


}
