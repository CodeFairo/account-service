package com.proyecto.account.infrastructure.adapter.out.kafka;

import com.proyecto.account.application.port.out.AccountCreatedEventPublisherPort;
import com.proyecto.account.avro.AccountCreatedEvent;
import com.proyecto.account.domain.model.Account;
import com.proyecto.customer.avro.CustomerCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountCreatedEventPublisherAdapter implements AccountCreatedEventPublisherPort {

    private static final String TOPIC = "account.created";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public Mono<Void> publish(Account account, CustomerCreatedEvent originEvent) {
        var event = AccountCreatedEvent.newBuilder()
                .setAccountId(account.getId())
                .setCustomerId(originEvent.getCustomerId())
                .setWalletId(originEvent.getWalletId())
                .setAccountType(account.getAccountType())
                .setStatus(account.getStatus())
                .build();

        log.info("📤 Publishing AccountCreatedEvent → topic [{}]: {}", TOPIC, event);

        return Mono.fromFuture(kafkaTemplate.send(TOPIC, account.getId(), event).toCompletableFuture())
                .doOnSuccess(result -> log.info("✅ AccountCreatedEvent published for wallet {}", originEvent.getWalletId()))
                .doOnError(e -> log.error("❌ Error publishing AccountCreatedEvent: {}", e.getMessage()))
                .then();
    }
}
