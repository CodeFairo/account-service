package com.proyecto.account.infrastructure.adapter.out.mongo;

import com.proyecto.account.domain.model.Transfer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface TransferMongoRepository extends ReactiveMongoRepository<Transfer, String> {
    Mono<Boolean> existsByOperationNumber(String operationNumber);
}
