package com.proyecto.account.application.port.in;

import com.proyecto.account.domain.model.Transfer;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface TransferUseCase {
    Mono<Transfer> transfer(String fromAccountId, String toAccountId, BigDecimal amount, String concept);
}
