package com.proyecto.account.infrastructure.adapter.out.mongo;

import com.proyecto.account.application.port.out.AccountRepositoryPort;
import com.proyecto.account.domain.model.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AccountRepositoryAdapter implements AccountRepositoryPort {

    private final AccountMongoRepository mongoRepository;

    @Override
    public Mono<Account> save(Account account) {
        return mongoRepository.save(account);
    }

    @Override
    public Mono<Account> findById(String id) {
        return mongoRepository.findById(id);
    }

    @Override
    public Flux<Account> findAll() {
        return mongoRepository.findAll();
    }

    @Override
    public Flux<Account> findByCustomerIdAndType(String customerId, String accountType) {
        return mongoRepository.findByCustomerIdsContainingAndAccountType(
                customerId, accountType.toUpperCase()
        );
    }
}
