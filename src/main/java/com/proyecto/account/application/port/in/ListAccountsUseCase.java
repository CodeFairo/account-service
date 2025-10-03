package com.proyecto.account.application.port.in;


import com.proyecto.account.domain.model.Account;
import reactor.core.publisher.Flux;

public interface ListAccountsUseCase {
    Flux<Account> findAll(String customerId, String accountType);
}
