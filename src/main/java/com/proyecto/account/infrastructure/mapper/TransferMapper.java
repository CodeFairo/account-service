package com.proyecto.account.infrastructure.mapper;

import com.proyecto.account.domain.model.Transfer;
import com.proyecto.account.model.TransferDTO;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.Optional;

@Component
public class TransferMapper {

    public TransferDTO toDto(Transfer transfer) {
        TransferDTO dto = new TransferDTO();
        dto.setId(transfer.getId());
        dto.setOperationNumber(transfer.getOperationNumber());
        dto.setFromAccountId(transfer.getFromAccountId());
        dto.setToAccountId(transfer.getToAccountId());
        dto.setAmount(transfer.getAmount().doubleValue());
        dto.setConcept(transfer.getConcept());
        dto.setWithdrawTransactionId(transfer.getWithdrawTransactionId());
        dto.setDepositTransactionId(transfer.getDepositTransactionId());
        dto.setDateTransfer(
                Optional.ofNullable(transfer.getDateTransfer())
                        .map(date -> date.atOffset(ZoneOffset.UTC))
                        .orElse(null)
        );
        return dto;
    }
}
