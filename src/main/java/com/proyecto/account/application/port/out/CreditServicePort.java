package com.proyecto.account.application.port.out;

import reactor.core.publisher.Mono;

public interface CreditServicePort {
    Mono<Boolean> hasAnyCredit(String customerId);
}
