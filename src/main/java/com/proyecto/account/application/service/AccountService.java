package com.proyecto.account.application.service;

import com.proyecto.account.application.port.in.CreateAccountUseCase;
import com.proyecto.account.application.port.in.GetAccountByIdUseCase;
import com.proyecto.account.application.port.in.ListAccountsUseCase;
import com.proyecto.account.application.port.out.AccountRepositoryPort;
import com.proyecto.account.application.port.out.CustomerServicePort;
import com.proyecto.account.application.strategy.AccountValidationFactory;
import com.proyecto.account.domain.model.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountService implements
        CreateAccountUseCase, GetAccountByIdUseCase, ListAccountsUseCase {

    private final AccountRepositoryPort accountRepositoryPort;
    private final CustomerServicePort customerServicePort;
    private final AccountValidationFactory accountValidationFactory;

    @Override
    public Mono<Account> create(Account account) {
        if (account.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            return Mono.error(new IllegalArgumentException("El saldo inicial no puede ser negativo"));
        }

        String mainCustomerId = account.getCustomerIds().getFirst();

        return customerServicePort.getCustomerType(mainCustomerId)
                .flatMap(customerType ->
                        accountValidationFactory.getStrategy(customerType).validate(account))
                .flatMap(accountRepositoryPort::save);
    }

    @Override
    public Mono<Account> findById(String accountId) {
        return accountRepositoryPort.findById(accountId);
    }

    @Override
    public Flux<Account> findAll(String customerId, String accountType) {
        if (customerId != null && accountType != null) {
            return accountRepositoryPort.findByCustomerIdAndType(customerId, accountType);
        }
        return accountRepositoryPort.findAll();
    }

    @Override
    public Mono<Account> createFromSaga(Account account) {
        if (account.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            return Mono.error(new IllegalArgumentException("El saldo inicial no puede ser negativo"));
        }
        return accountRepositoryPort.save(account);
    }


}