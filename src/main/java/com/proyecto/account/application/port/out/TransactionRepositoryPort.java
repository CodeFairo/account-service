package com.proyecto.account.application.port.out;

import com.proyecto.account.domain.model.Transaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransactionRepositoryPort {
    Mono<Transaction> save(Transaction tx);
    Flux<Transaction> findByAccountId(String accountId);
}
