package com.proyecto.account.infrastructure.adapter.out.mongo;

import com.proyecto.account.application.port.out.TransferRepositoryPort;
import com.proyecto.account.domain.model.Transfer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class TransferRepositoryAdapter implements TransferRepositoryPort {

    private final TransferMongoRepository repository;

    @Override
    public Mono<Transfer> save(Transfer transfer) {
        return repository.save(transfer);
    }

    @Override
    public Mono<Boolean> existsByOperationNumber(String operationNumber) {
        return repository.existsByOperationNumber(operationNumber);
    }
}
