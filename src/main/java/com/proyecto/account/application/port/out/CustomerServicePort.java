package com.proyecto.account.application.port.out;

import reactor.core.publisher.Mono;

public interface CustomerServicePort {
    Mono<String> getCustomerType(String customerId);
}
