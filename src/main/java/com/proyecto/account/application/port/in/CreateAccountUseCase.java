package com.proyecto.account.application.port.in;

import com.proyecto.account.domain.model.Account;
import reactor.core.publisher.Mono;

public interface CreateAccountUseCase {
    Mono<Account> create(Account account);
}
