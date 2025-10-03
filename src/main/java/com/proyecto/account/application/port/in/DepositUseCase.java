package com.proyecto.account.application.port.in;


import com.proyecto.account.domain.model.Transaction;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface DepositUseCase {
    Mono<Transaction> deposit(String accountId, BigDecimal amount, String concept);
}