package com.proyecto.account.application.strategy.impl;

import com.proyecto.account.application.port.out.AccountRepositoryPort;
import com.proyecto.account.application.port.out.CreditServicePort;
import com.proyecto.account.application.strategy.AccountValidationStrategy;
import com.proyecto.account.domain.model.Account;
import com.proyecto.account.model.AccountRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class PersonalAccountValidationStrategy implements AccountValidationStrategy {

    private final AccountRepositoryPort accountRepositoryPort;
    private final CreditServicePort creditServicePort;

    @Override
    public boolean supports(String customerType) {
        return "PERSONAL".equalsIgnoreCase(customerType);
    }

    @Override
    public Mono<Account> validate(Account account) {
        String customerId = account.getCustomerIds().getFirst();

        // Primero, validar si tiene algún crédito activo
        return creditServicePort.hasAnyCredit(customerId)
                .flatMap(hasCredit -> {
                    if (hasCredit) {
                        return Mono.error(new IllegalArgumentException(
                                "El cliente no puede aperturar una cuenta si posee créditos vencidos"));
                    }

                    // Luego, validar que no tenga otra cuenta del mismo tipo
                    return accountRepositoryPort.findByCustomerIdAndType(customerId, account.getAccountType())
                            .hasElements()
                            .flatMap(alreadyExists -> {
                                if (alreadyExists) {
                                    return Mono.error(new IllegalArgumentException(
                                            "El cliente PERSONAL solo puede tener una cuenta de tipo "
                                                    + account.getAccountType()));
                                }

                                // Configuración según el tipo de cuenta
                                switch (AccountRequestDTO.AccountTypeEnum.valueOf(account.getAccountType())) {
                                    case SAVINGS -> {
                                        account.setMaintenanceFee(BigDecimal.ZERO);
                                        account.setMonthlyMovementLimit(20);
                                    }
                                    case CHECKING -> {
                                        account.setMaintenanceFee(new BigDecimal("10.00"));
                                        account.setMonthlyMovementLimit(null);
                                    }
                                    case TERM -> {
                                        account.setMaintenanceFee(BigDecimal.ZERO);
                                        account.setOperationDay(LocalDate.now());
                                        account.setMonthlyMovementLimit(null);
                                    }
                                }

                                account.setMovementsThisMonth(0);
                                return Mono.just(account);
                            });
                });
    }
}
