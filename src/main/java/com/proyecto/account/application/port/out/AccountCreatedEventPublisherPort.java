package com.proyecto.account.application.port.out;

import com.proyecto.account.domain.model.Account;
import com.proyecto.customer.avro.CustomerCreatedEvent;
import reactor.core.publisher.Mono;

public interface AccountCreatedEventPublisherPort {
    Mono<Void> publish(Account account, CustomerCreatedEvent originEvent);
}
