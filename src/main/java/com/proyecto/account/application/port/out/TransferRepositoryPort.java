package com.proyecto.account.application.port.out;

import com.proyecto.account.domain.model.Transfer;
import reactor.core.publisher.Mono;

public interface TransferRepositoryPort {
    Mono<Transfer> save(Transfer transfer);
    Mono<Boolean> existsByOperationNumber(String operationNumber);
}
