package com.proyecto.account.application.port.in;

import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface BalanceUseCase {
    Mono<BigDecimal> getBalance(String accountId);
}