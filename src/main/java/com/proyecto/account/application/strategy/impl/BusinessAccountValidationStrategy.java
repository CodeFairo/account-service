package com.proyecto.account.application.strategy.impl;

import com.proyecto.account.application.port.out.CreditServicePort;
import com.proyecto.account.application.strategy.AccountValidationStrategy;
import com.proyecto.account.domain.model.Account;
import com.proyecto.account.model.AccountRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class BusinessAccountValidationStrategy implements AccountValidationStrategy {

    private final CreditServicePort creditServicePort;

    @Override
    public boolean supports(String customerType) {
        return "BUSINESS".equalsIgnoreCase(customerType);
    }

    @Override
    public Mono<Account> validate(Account account) {
        String customerId = account.getCustomerIds().getFirst();

        // Validar si tiene algún crédito activo
        return creditServicePort.hasAnyCredit(customerId)
                .flatMap(hasCredit -> {
                    if (hasCredit) {
                        return Mono.error(new IllegalArgumentException(
                                "El cliente no puede aperturar una cuenta si posee créditos vencidos"));
                    }

                    // Validar tipos de cuenta no permitidos
                    if (AccountRequestDTO.AccountTypeEnum.SAVINGS.name().equalsIgnoreCase(account.getAccountType())
                            || AccountRequestDTO.AccountTypeEnum.TERM.name().equalsIgnoreCase(account.getAccountType())) {
                        return Mono.error(new IllegalArgumentException(
                                "Los clientes empresariales no pueden tener cuentas de ahorro ni a plazo fijo"));
                    }

                    // Configuración de cuenta BUSINESS
                    account.setMaintenanceFee(new BigDecimal("10.00"));
                    account.setMonthlyMovementLimit(null);
                    account.setMovementsThisMonth(0);
                    return Mono.just(account);
                });
    }
}
