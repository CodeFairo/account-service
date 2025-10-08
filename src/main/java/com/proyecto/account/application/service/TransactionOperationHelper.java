package com.proyecto.account.application.service;

import com.proyecto.account.application.policy.AccountTransactionValidator;
import com.proyecto.account.application.port.out.AccountRepositoryPort;
import com.proyecto.account.application.port.out.TransactionRepositoryPort;
import com.proyecto.account.domain.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TransactionOperationHelper {

    private final AccountRepositoryPort accountRepo;
    private final TransactionRepositoryPort txRepo;
    private final AccountTransactionValidator validator;

    /**
     * Ejecuta un depósito con un número de operación existente.
     */
    public Mono<Transaction> depositWithOp(String accountId, BigDecimal amount, String concept, String operationNumber) {
        return accountRepo.findById(accountId)
                .flatMap(acc ->
                        validator.validate(acc, "DEPOSIT")
                                .flatMap(validAcc -> {
                                    validAcc.setBalance(validAcc.getBalance().add(amount));

                                    if ("SAVINGS".equalsIgnoreCase(validAcc.getAccountType())) {
                                        validAcc.setMovementsThisMonth(validAcc.getMovementsThisMonth() + 1);
                                    }

                                    return accountRepo.save(validAcc)
                                            .flatMap(saved -> txRepo.save(Transaction.builder()
                                                    .operationNumber(operationNumber)
                                                    .accountId(accountId)
                                                    .type("DEPOSIT")
                                                    .amount(amount)
                                                    .concept(concept)
                                                    .balanceAfter(saved.getBalance())
                                                    .dateTransaction(LocalDateTime.now())
                                                    .build()));
                                })
                );
    }

    /**
     * Ejecuta un retiro con un número de operación existente.
     */
    public Mono<Transaction> withdrawWithOp(String accountId, BigDecimal amount, String concept, String operationNumber) {
        return accountRepo.findById(accountId)
                .flatMap(acc -> {
                    if (acc.getBalance().compareTo(amount) < 0) {
                        return Mono.error(new IllegalStateException("Fondos insuficientes"));
                    }

                    return validator.validate(acc, "WITHDRAW")
                            .flatMap(validAcc -> {
                                validAcc.setBalance(validAcc.getBalance().subtract(amount));

                                if ("SAVINGS".equalsIgnoreCase(validAcc.getAccountType())) {
                                    validAcc.setMovementsThisMonth(validAcc.getMovementsThisMonth() + 1);
                                }

                                return accountRepo.save(validAcc)
                                        .flatMap(saved -> txRepo.save(Transaction.builder()
                                                .operationNumber(operationNumber)
                                                .accountId(accountId)
                                                .type("WITHDRAW")
                                                .amount(amount)
                                                .concept(concept)
                                                .balanceAfter(saved.getBalance())
                                                .dateTransaction(LocalDateTime.now())
                                                .build()));
                            });
                });
    }
}
