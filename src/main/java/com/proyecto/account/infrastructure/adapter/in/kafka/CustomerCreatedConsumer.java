package com.proyecto.account.infrastructure.adapter.in.kafka;

import com.proyecto.account.application.port.out.AccountCreatedEventPublisherPort;
import com.proyecto.account.application.service.AccountService;
import com.proyecto.account.domain.model.Account;
import com.proyecto.customer.avro.CustomerCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerCreatedConsumer {

    private final AccountService accountService;
    private final AccountCreatedEventPublisherPort eventPublisher;

    @KafkaListener(topics = "customer.created", groupId = "account-service")
    public void handleCustomerCreated(CustomerCreatedEvent event) {
        log.info("📥 Received CustomerCreatedEvent: {}", event);

        //Construir la cuenta base desde el evento
        Account account = new Account();
        account.setCustomerIds(List.of(event.getCustomerId().toString()));
        account.setAccountType("SAVINGS");
        account.setStatus("ACTIVE");
        account.setBalance(BigDecimal.ZERO);

        // ⚙️ Flujo reactivo completo
        Mono.defer(() -> accountService.createFromSaga(account))
                .flatMap(created -> eventPublisher.publish(created, event))
                .doOnSuccess(v -> log.info(" Account created and event published for wallet {}", event.getWalletId()))
                .doOnError(err -> log.error(" Error processing account creation for customer {}: {}", event.getCustomerId(), err.getMessage()))
                .subscribe();
    }
}
