package com.proyecto.account.infrastructure.adapter.out.mongo;

import com.proyecto.account.domain.model.Account;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface AccountMongoRepository extends ReactiveMongoRepository<Account, String> {

    Flux<Account> findByCustomerIdsContainingAndAccountType(String customerId, String accountType);
}