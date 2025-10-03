package com.proyecto.account.application.service;

import com.proyecto.account.application.port.in.*;
import com.proyecto.account.application.port.out.AccountRepositoryPort;
import com.proyecto.account.application.port.out.TransactionRepositoryPort;
import com.proyecto.account.domain.model.Account;
import com.proyecto.account.domain.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionService implements
        DepositUseCase, WithdrawUseCase, BalanceUseCase, TransactionsHistoryUseCase {

    private final AccountRepositoryPort accountRepo;
    private final TransactionRepositoryPort txRepo;

    @Override
    public Mono<Transaction> deposit(String accountId, BigDecimal amount, String concept) {
        return accountRepo.findById(accountId)
                .flatMap(acc -> {
                    // reglas según tipo de cuenta
                    return validateAccountTransaction(acc, "DEPOSIT")
                            .flatMap(validAcc -> {
                                validAcc.setBalance(validAcc.getBalance().add(amount));

                                // actualizar contador de movimientos si es ahorro
                                if ("SAVINGS".equalsIgnoreCase(validAcc.getAccountType())) {
                                    validAcc.setMovementsThisMonth(validAcc.getMovementsThisMonth() + 1);
                                }

                                return accountRepo.save(validAcc)
                                        .flatMap(saved -> txRepo.save(Transaction.builder()
                                                .accountId(accountId)
                                                .type("DEPOSIT")
                                                .amount(amount)
                                                .concept(concept)
                                                .balanceAfter(saved.getBalance())
                                                .dateTransaction(LocalDateTime.now())
                                                .build()));
                            });
                });
    }

    @Override
    public Mono<Transaction> withdraw(String accountId, BigDecimal amount, String concept) {
        return accountRepo.findById(accountId)
                .flatMap(acc -> {
                    if (acc.getBalance().compareTo(amount) < 0) {
                        return Mono.error(new IllegalStateException("Fondos insuficientes"));
                    }

                    return validateAccountTransaction(acc, "WITHDRAW")
                            .flatMap(validAcc -> {
                                validAcc.setBalance(validAcc.getBalance().subtract(amount));

                                // actualizar contador de movimientos si es ahorro
                                if ("SAVINGS".equalsIgnoreCase(validAcc.getAccountType())) {
                                    validAcc.setMovementsThisMonth(validAcc.getMovementsThisMonth() + 1);
                                }

                                return accountRepo.save(validAcc)
                                        .flatMap(saved -> txRepo.save(Transaction.builder()
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

    private Mono<Account> validateAccountTransaction(Account acc, String txType) {
        String type = acc.getAccountType();

        // Ahorro: validar límite de movimientos
        if ("SAVINGS".equalsIgnoreCase(type)) {
            if (acc.getMovementsThisMonth() >= acc.getMonthlyMovementLimit()) {
                return Mono.error(new IllegalStateException(
                        "La cuenta de ahorro ha superado el límite mensual de movimientos"));
            }
            return Mono.just(acc);
        }

        // Corriente: nada que ver solo devulve al cuienta
        if ("CHECKING".equalsIgnoreCase(type)) {
            return Mono.just(acc);
        }

        // Plazo fijo: solo un movimiento en un día específico
        if ("TERM".equalsIgnoreCase(type)) {
            if (acc.getOperationDay() == null) {
                return Mono.error(new IllegalStateException(
                        "La cuenta a plazo fijo no tiene configurado un día de operación"));
            }

            int diaOperacion = acc.getOperationDay().getDayOfMonth();
            int diaHoy = LocalDate.now().getDayOfMonth();

            if (diaOperacion != diaHoy) {
                return Mono.error(new IllegalStateException(
                        "Las operaciones en cuentas a plazo fijo solo pueden realizarse el día "
                                + diaOperacion + " de cada mes"));
            }

            // solo se permite 1 movimiento en ese día: (falta logica no se implementó)
            /*return txRepo.findByAccountId(acc.getId())
                    .filter(tx -> tx.getTimestamp().toLocalDate().equals(LocalDate.now()))
                    .hasElements()
                    .flatMap(alreadyHasTxToday -> {
                        if (alreadyHasTxToday) {
                            return Mono.error(new IllegalStateException(
                                    "La cuenta a plazo fijo solo permite un movimiento por día"));
                        }
                        return Mono.just(acc);
                    });*/

            return Mono.just(acc);
        }

        // Si llega aquí, no era SAVINGS, CHECKING ni TERM
        return Mono.error(new IllegalStateException("Tipo de cuenta no soportado: " + type));
    }

    @Override
    public Mono<BigDecimal> getBalance(String accountId) {
        return accountRepo.findById(accountId).map(Account::getBalance);
    }

    @Override
    public Flux<Transaction> getTransactions(String accountId) {
        return txRepo.findByAccountId(accountId);
    }
}
