package com.proyecto.account.application.port.in;

import com.proyecto.account.domain.model.Transaction;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface WithdrawUseCase {
    Mono<Transaction> withdraw(String accountId, BigDecimal amount, String concept);
}