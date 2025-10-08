package com.proyecto.account.application.service;

import com.proyecto.account.application.port.in.*;
import com.proyecto.account.application.port.out.AccountRepositoryPort;
import com.proyecto.account.application.port.out.TransactionRepositoryPort;
import com.proyecto.account.domain.model.Account;
import com.proyecto.account.domain.model.Transaction;
import com.proyecto.account.domain.util.OperationNumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransactionService implements
        DepositUseCase, WithdrawUseCase, BalanceUseCase, TransactionsHistoryUseCase {

    private final TransactionOperationHelper helper;
    private final AccountRepositoryPort accountRepo;
    private final TransactionRepositoryPort txRepo;

    @Override
    public Mono<Transaction> deposit(String accountId, BigDecimal amount, String concept) {
        String op = OperationNumberGenerator.generate();
        return helper.depositWithOp(accountId, amount, concept, op);
    }

    @Override
    public Mono<Transaction> withdraw(String accountId, BigDecimal amount, String concept) {
        String op = OperationNumberGenerator.generate();
        return helper.withdrawWithOp(accountId, amount, concept, op);
    }

    @Override
    public Mono<java.math.BigDecimal> getBalance(String accountId) {
        return accountRepo.findById(accountId).map(Account::getBalance);
    }

    @Override
    public Flux<Transaction> getTransactions(String accountId) {
        return txRepo.findByAccountId(accountId);
    }
}
