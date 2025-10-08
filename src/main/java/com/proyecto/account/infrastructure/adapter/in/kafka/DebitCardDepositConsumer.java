package com.proyecto.account.infrastructure.adapter.in.kafka;

import com.proyecto.account.avro.AccountTransactionCompletedEvent;
import com.proyecto.account.avro.DebitCardDepositEvent;
import com.proyecto.account.application.service.TransactionService;
import com.proyecto.account.domain.model.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class DebitCardDepositConsumer {

    private final TransactionService transactionService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC = "account.transactions.completed";

    @KafkaListener(topics = "debitcard.deposits", groupId = "account-service")
    public void handleDebitDeposit(DebitCardDepositEvent event) {
        log.info("Received DebitCardDepositEvent via Avro: {}", event);

        BigDecimal amount = BigDecimal.valueOf(event.getAmount());
        String concept = event.getDescription() != null ? event.getDescription().toString() : "Debit card deposit";

        Mono.defer(() ->
                        transactionService.deposit(event.getAccountId().toString(), amount, concept)
                )
                .flatMap(tx -> publishTransactionCompleted(tx, event.getCardId().toString()))
                .doOnSuccess(v -> log.info("Deposit applied and event published for card {}", event.getCardId()))
                .doOnError(err -> log.error("Error processing deposit for account {}: {}", event.getAccountId(), err.getMessage()))
                .subscribe();
    }

    private Mono<Void> publishTransactionCompleted(Transaction tx, String cardId) {
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
