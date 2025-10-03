package com.proyecto.account.infrastructure.mapper;

import com.proyecto.account.domain.model.Account;
import com.proyecto.account.model.AccountDTO;
import com.proyecto.account.model.AccountRequestDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AccountMapper {
    public Account toDomain(AccountRequestDTO dto) {
        return Account.builder()
                .accountType(dto.getAccountType().name())
                .customerIds(dto.getCustomerIds())
                .balance(dto.getInitialDeposit() != null
                        ? BigDecimal.valueOf(dto.getInitialDeposit())
                        : BigDecimal.ZERO)
                .status("ACTIVE")
                .build();
    }

    public AccountDTO toDto(Account account) {
        AccountDTO dto = new AccountDTO();
        dto.setId(account.getId());
        dto.setAccountType(account.getAccountType());
        dto.setCustomerIds(account.getCustomerIds());
        dto.setBalance(account.getBalance().doubleValue());
        dto.setStatus(AccountDTO.StatusEnum.valueOf(account.getStatus()));
        return dto;
    }
}
