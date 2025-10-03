package com.proyecto.account.infrastructure.adapter.in.web;

import com.proyecto.account.api.AccountsApi;
import com.proyecto.account.infrastructure.mapper.AccountMapper;
import com.proyecto.account.application.port.in.*;

import com.proyecto.account.model.AccountDTO;
import com.proyecto.account.model.AccountRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AccountsController implements AccountsApi {

    private final CreateAccountUseCase createAccountUseCase;
    private final GetAccountByIdUseCase getAccountByIdUseCase;
    private final ListAccountsUseCase listAccountsUseCase;
    /*private final DepositUseCase depositUseCase;
    private final WithdrawUseCase withdrawUseCase;
    private final BalanceUseCase balanceUseCase;
    private final TransactionsHistoryUseCase transactionsHistoryUseCase;*/

    private final AccountMapper accountMapper;
    //private final TransactionMapper transactionMapper;

    @Override
    public Mono<ResponseEntity<AccountDTO>> accountsPost(
            Mono<AccountRequestDTO> accountRequestDto, ServerWebExchange exchange) {
        return accountRequestDto
                .map(accountMapper::toDomain)
                .flatMap(createAccountUseCase::create)
                .map(accountMapper::toDto)
                .map(acc -> ResponseEntity.status(HttpStatus.CREATED).body(acc));
    }

    @Override
    public Mono<ResponseEntity<Flux<AccountDTO>>> accountsGet(
            String customerId, String accountType, ServerWebExchange exchange) {
        Flux<AccountDTO> accounts = listAccountsUseCase.findAll(customerId, accountType)
                .map(accountMapper::toDto);
        return Mono.just(ResponseEntity.ok(accounts));
    }

    @Override
    public Mono<ResponseEntity<AccountDTO>> accountsAccountIdGet(
            String accountId, ServerWebExchange exchange) {
        return getAccountByIdUseCase.findById(accountId)
                .map(accountMapper::toDto)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

}
