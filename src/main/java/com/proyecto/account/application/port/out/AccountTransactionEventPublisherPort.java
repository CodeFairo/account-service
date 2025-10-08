package com.proyecto.account.application.port.out;

import com.proyecto.account.domain.model.Transaction;
import reactor.core.publisher.Mono;

public interface AccountTransactionEventPublisherPort {
    Mono<Void> publishTransactionCompleted(Transaction tx, String cardId);
}
