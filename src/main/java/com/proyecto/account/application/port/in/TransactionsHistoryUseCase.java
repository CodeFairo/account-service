package com.proyecto.account.application.port.in;

import com.proyecto.account.domain.model.Transaction;
import reactor.core.publisher.Flux;

public interface TransactionsHistoryUseCase {
    Flux<Transaction> getTransactions(String accountId);
}