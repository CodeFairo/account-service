package com.proyecto.account.infrastructure.adapter.out.mongo;

import com.proyecto.account.application.port.out.TransactionRepositoryPort;
import com.proyecto.account.domain.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class TransactionRepositoryAdapter implements TransactionRepositoryPort {

    private final TransactionMongoRepository mongoRepository;

    @Override
    public Mono<Transaction> save(Transaction tx) {
        return mongoRepository.save(tx);
    }

    @Override
    public Flux<Transaction> findByAccountId(String accountId) {
        return mongoRepository.findByAccountId(accountId);
    }
}
