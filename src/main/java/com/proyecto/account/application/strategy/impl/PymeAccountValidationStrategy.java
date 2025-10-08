package com.proyecto.account.application.strategy.impl;

import com.proyecto.account.application.port.out.CardServicePort;
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
public class PymeAccountValidationStrategy implements AccountValidationStrategy {

    private final CardServicePort cardServicePort;
    private final CreditServicePort creditServicePort;

    @Override
    public boolean supports(String customerType) {
        return "PYME".equalsIgnoreCase(customerType);
    }

    @Override
    public Mono<Account> validate(Account account) {
        // Solo se permiten cuentas corrientes
        if (!AccountRequestDTO.AccountTypeEnum.CHECKING.name()
                .equalsIgnoreCase(account.getAccountType())) {
            return Mono.error(new IllegalArgumentException(
                    "Los clientes PYME solo pueden abrir cuentas corrientes"));
        }

        String customerId = account.getCustomerIds().getFirst();

        // Verificar si tiene algún crédito activo
        return creditServicePort.hasAnyCredit(customerId)
                .flatMap(hasCredit -> {
                    if (hasCredit) {
                        return Mono.error(new IllegalArgumentException(
                                "El cliente no puede aperturar una cuenta si posee créditos vencidos"));
                    }

                    // Verificar si tiene tarjeta de crédito activa
                    return cardServicePort.hasAnyCard(customerId)
                            .flatMap(hasCard -> {
                                if (!hasCard) {
                                    return Mono.error(new IllegalArgumentException(
                                            "El cliente PYME debe tener al menos una tarjeta de crédito activa"));
                                }

                                // Configuración de cuenta PYME
                                account.setMaintenanceFee(BigDecimal.ZERO);
                                account.setMonthlyMovementLimit(null);
                                account.setMovementsThisMonth(0);
                                return Mono.just(account);
                            });
                });
    }
}
