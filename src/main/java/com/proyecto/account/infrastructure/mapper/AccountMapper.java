package com.proyecto.account.infrastructure.mapper;

import com.proyecto.account.domain.model.Account;
import com.proyecto.account.model.AccountDTO;
import com.proyecto.account.model.AccountRequestDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

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
        if (account == null) {
            return null;
        }

        AccountDTO dto = new AccountDTO();

        dto.setId(account.getId());
        dto.setAccountType(account.getAccountType());
        dto.setCustomerIds(account.getCustomerIds());
        dto.setStatus(
                Optional.ofNullable(account.getStatus())
                        .map(AccountDTO.StatusEnum::valueOf)
                        .orElse(null)
        );

        dto.setBalance(
                Optional.ofNullable(account.getBalance())
                        .map(BigDecimal::doubleValue)
                        .orElse(0.0)
        );

        dto.setMinimumAverageBalance(
                Optional.ofNullable(account.getMinimumAverageBalance())
                        .map(BigDecimal::doubleValue)
                        .orElse(0.0)
        );

        dto.setMonthlyMovementLimit(
                Optional.ofNullable(account.getMonthlyMovementLimit())
                        .orElse(0)
        );

        dto.setMovementsThisMonth(
                Optional.ofNullable(account.getMovementsThisMonth())
                        .orElse(0)
        );

        dto.setOperationDay(
                Optional.ofNullable(account.getOperationDay())
                        .map(LocalDate::getDayOfMonth)
                        .map(String::valueOf)
                        .orElse(" ")
        );

        dto.setMaintenanceFee(
                Optional.ofNullable(account.getMaintenanceFee())
                        .map(BigDecimal::doubleValue)
                        .orElse(0.0)
        );

        return dto;
    }

}
