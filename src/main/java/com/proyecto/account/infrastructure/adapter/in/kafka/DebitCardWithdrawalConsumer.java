package com.proyecto.account.infrastructure.adapter.in.kafka;

import com.proyecto.account.application.port.out.AccountTransactionEventPublisherPort;
import com.proyecto.account.avro.DebitCardWithdrawalEvent;
import com.proyecto.account.application.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class DebitCardWithdrawalConsumer {

    private final TransactionService transactionService;
    private final AccountTransactionEventPublisherPort eventPublisher;

    @KafkaListener(topics = "debitcard.withdrawals", groupId = "account-service")
    public void handleDebitWithdrawal(DebitCardWithdrawalEvent event) {
        log.info("Received DebitCardWithdrawalEvent via Avro: {}", event);

        BigDecimal amount = BigDecimal.valueOf(event.getAmount());
        String concept = event.getDescription() != null ? event.getDescription().toString() : "Debit card withdrawal";

        Mono.defer(() -> transactionService.withdraw(event.getAccountId().toString(), amount, concept))
                .flatMap(tx -> eventPublisher.publishTransactionCompleted(tx, event.getCardId().toString()))
                .doOnSuccess(v -> log.info(" Withdrawal processed and event published for card {}", event.getCardId()))
                .doOnError(err -> log.error(" Error processing withdrawal for account {}: {}", event.getAccountId(), err.getMessage()))
                .subscribe();
    }
}



/*package com.proyecto.account.infrastructure.adapter.in.kafka;

import com.proyecto.account.avro.AccountTransactionCompletedEvent;
import com.proyecto.account.avro.DebitCardWithdrawalEvent;
import com.proyecto.account.application.service.TransactionService;
import com.proyecto.account.domain.model.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class DebitCardWithdrawalConsumer {

    private final TransactionService transactionService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC = "account.transactions.completed";

    @KafkaListener(topics = "debitcard.withdrawals", groupId = "account-service")
    public void handleDebitWithdrawal(DebitCardWithdrawalEvent event) {
        log.info("Received DebitCardWithdrawalEvent via Avro: {}", event);

        BigDecimal amount = BigDecimal.valueOf(event.getAmount());
        String concept = event.getDescription() != null ? event.getDescription().toString() : "Debit card withdrawal";

        Mono.defer(() ->
                        transactionService.withdraw(event.getAccountId().toString(), amount, concept)
                )
                .flatMap(tx -> publishTransactionCompleted(tx, event.getCardId().toString()))
                .doOnSuccess(v -> log.info("Withdrawal and event published for card {}", event.getCardId()))
                .doOnError(err -> log.error("Error processing withdrawal for account {}: {}", event.getAccountId(), err.getMessage()))
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
*/