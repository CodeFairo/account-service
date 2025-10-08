package com.proyecto.account.application.strategy;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class AccountValidationFactory {

    private final List<AccountValidationStrategy> strategies;

    public AccountValidationFactory(List<AccountValidationStrategy> strategies) {
        this.strategies = strategies;
    }

    public AccountValidationStrategy getStrategy(String customerType) {
        return strategies.stream()
                .filter(s -> s.supports(customerType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Tipo de cliente no soportado: " + customerType));
    }
}
