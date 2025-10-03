package com.proyecto.account.application.port.out;

import com.proyecto.account.domain.model.Account;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountRepositoryPort {
    Mono<Account> save(Account account);
    Mono<Account> findById(String id);
    Flux<Account> findAll();
    Flux<Account> findByCustomerIdAndType(String customerId, String accountType);
}