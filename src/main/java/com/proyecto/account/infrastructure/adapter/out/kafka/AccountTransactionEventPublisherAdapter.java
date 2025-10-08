package com.proyecto.account.infrastructure.adapter.out.kafka;

import com.proyecto.account.application.port.out.AccountTransactionEventPublisherPort;
import com.proyecto.account.domain.model.Transaction;
import com.proyecto.account.avro.AccountTransactionCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountTransactionEventPublisherAdapter implements AccountTransactionEventPublisherPort {

    private static final String TOPIC = "account.transactions.completed";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public Mono<Void> publishTransactionCompleted(Transaction tx, String cardId) {
        var event = AccountTransactionCompletedEvent.newBuilder()
                .setOperationNumber(tx.getOperationNumber())
                .setAccountId(tx.getAccountId())
                .setCardId(cardId)
                .setAmount(tx.getAmount().doubleValue())
                .setConcept(tx.getConcept())
                .setTransactionType(tx.getType())
                .setTimestamp(OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .build();

        log.info("Publishing AccountTransactionCompletedEvent to topic {}: {}", TOPIC, event);

        return Mono.fromFuture(kafkaTemplate.send(TOPIC, tx.getAccountId(), event).toCompletableFuture())
                .then();
    }
}
