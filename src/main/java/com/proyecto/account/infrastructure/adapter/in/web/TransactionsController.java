package com.proyecto.account.infrastructure.adapter.in.web;

import com.proyecto.account.api.TransactionsApi;
import com.proyecto.account.infrastructure.mapper.TransactionMapper;
import com.proyecto.account.application.port.in.*;

import com.proyecto.account.model.AccountsAccountIdBalanceGet200Response;

import com.proyecto.account.model.TransactionDTO;
import com.proyecto.account.model.TransactionRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class TransactionsController implements TransactionsApi {

    private final DepositUseCase depositUseCase;
    private final WithdrawUseCase withdrawUseCase;
    private final BalanceUseCase balanceUseCase;
    private final TransactionsHistoryUseCase transactionsHistoryUseCase;
    private final TransactionMapper transactionMapper;

    @Override
    public Mono<ResponseEntity<AccountsAccountIdBalanceGet200Response>> accountsAccountIdBalanceGet(
            String accountId, ServerWebExchange exchange) {
        return balanceUseCase.getBalance(accountId)
                .map(balance -> {
                    AccountsAccountIdBalanceGet200Response resp = new AccountsAccountIdBalanceGet200Response();
                    resp.setAccountId(accountId);
                    resp.setAvailableBalance(balance.doubleValue());
                    return ResponseEntity.ok(resp);
                })
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @Override
    public Mono<ResponseEntity<TransactionDTO>> accountsAccountIdDepositPost(
            String accountId, Mono<TransactionRequestDTO> request, ServerWebExchange exchange) {
        return request
                .flatMap(req -> depositUseCase.deposit(accountId, BigDecimal.valueOf(req.getAmount()), req.getConcept()))
                .map(transactionMapper::toDto)
                .map(tx -> ResponseEntity.status(HttpStatus.CREATED).body(tx));
    }

    @Override
    public Mono<ResponseEntity<Flux<TransactionDTO>>> accountsAccountIdTransactionsGet(
            String accountId, ServerWebExchange exchange) {
        Flux<TransactionDTO> txs = transactionsHistoryUseCase.getTransactions(accountId)
                .map(transactionMapper::toDto);
        return Mono.just(ResponseEntity.ok(txs));
    }

    @Override
    public Mono<ResponseEntity<TransactionDTO>> accountsAccountIdWithdrawPost(
            String accountId, Mono<TransactionRequestDTO> request, ServerWebExchange exchange) {
        return request
                .flatMap(req -> withdrawUseCase.withdraw(accountId, BigDecimal.valueOf(req.getAmount()), req.getConcept()))
                .map(transactionMapper::toDto)
                .map(tx -> ResponseEntity.status(HttpStatus.CREATED).body(tx));
    }
}
