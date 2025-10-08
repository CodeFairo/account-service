package com.proyecto.account.application.port.out;

import reactor.core.publisher.Mono;

public interface CardServicePort {
    Mono<Boolean> hasAnyCard(String customerId);
}
