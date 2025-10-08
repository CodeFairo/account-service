package com.proyecto.account.application.strategy;

import com.proyecto.account.domain.model.Account;
import reactor.core.publisher.Mono;

public interface AccountValidationStrategy {
    boolean supports(String customerType);
    Mono<Account> validate(Account account);
}
