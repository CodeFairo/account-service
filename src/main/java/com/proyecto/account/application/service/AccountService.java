package com.proyecto.account.application.service;

import com.proyecto.account.application.port.in.CreateAccountUseCase;
import com.proyecto.account.application.port.in.GetAccountByIdUseCase;
import com.proyecto.account.application.port.in.ListAccountsUseCase;
import com.proyecto.account.application.port.out.AccountRepositoryPort;
import com.proyecto.account.application.port.out.CustomerServicePort;
import com.proyecto.account.domain.model.Account;
import com.proyecto.account.model.AccountRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AccountService implements
        CreateAccountUseCase,
        GetAccountByIdUseCase,
        ListAccountsUseCase {

    private final AccountRepositoryPort accountRepositoryPort;
    private final CustomerServicePort customerServicePort;

    @Override
    public Mono<Account> create(Account account) {
        //System.out.println("entra aqui " + account.getAccountType());

        // regla común: no permitir saldos negativos
        if (account.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            return Mono.error(new IllegalArgumentException("El saldo inicial no puede ser negativo"));
        }

        // validar reglas de negocio según tipo de cliente y tipo de cuenta
        return validateAccountRules(account)
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

    private Mono<Account> validateAccountRules(Account account) {
        String mainCustomerId = account.getCustomerIds().getFirst();

        return customerServicePort.getCustomerType(mainCustomerId)
                .flatMap(customerType -> {
                    //System.out.println("customerType: " + customerType);

                    // === REGLAS PARA CLIENTE EMPRESARIAL ===
                    if ("BUSINESS".equalsIgnoreCase(customerType)) {
                        // ❌ No puede tener SAVINGS ni TERM
                        if (AccountRequestDTO.AccountTypeEnum.SAVINGS.name().equalsIgnoreCase(account.getAccountType())
                                || AccountRequestDTO.AccountTypeEnum.TERM.name().equalsIgnoreCase(account.getAccountType())) {
                            return Mono.error(new IllegalArgumentException(
                                    "Clientes empresariales no pueden tener cuentas de ahorro ni a plazo fijo"));
                        }

                        // ✅ Puede tener múltiples CHECKING
                        if (AccountRequestDTO.AccountTypeEnum.CHECKING.name().equalsIgnoreCase(account.getAccountType())) {
                            if (account.getCustomerIds() == null || account.getCustomerIds().isEmpty()) {
                                return Mono.error(new IllegalArgumentException(
                                        "Las cuentas empresariales deben tener al menos un titular"));
                            }
                            // firmantes pueden ser 0 o más
                            account.setMaintenanceFee(new BigDecimal("10.00")); // comisión ejemplo
                            account.setMonthlyMovementLimit(null);              // sin límite
                            return Mono.just(account);
                        }
                    }

                    // === REGLAS PARA CLIENTE PERSONAL ===
                    if ("PERSONAL".equalsIgnoreCase(customerType)) {
                        // Debe validar que no tenga YA una cuenta de ese tipo
                        return accountRepositoryPort.findByCustomerIdAndType(mainCustomerId, account.getAccountType())
                                .hasElements()
                                .flatMap(alreadyHasType -> {
                                    if (alreadyHasType) {
                                        return Mono.error(new IllegalArgumentException(
                                                "El cliente personal solo puede tener una cuenta de tipo " + account.getAccountType()));
                                    }

                                    // ⚡ reglas extra si la cuenta es SAVINGS
                                    if (AccountRequestDTO.AccountTypeEnum.SAVINGS.name().equalsIgnoreCase(account.getAccountType())) {
                                        account.setMaintenanceFee(BigDecimal.ZERO);  // sin comisión
                                        account.setMonthlyMovementLimit(20);         // 20 movimientos por mes
                                        account.setMovementsThisMonth(0);
                                    }

                                    // ⚡ reglas para CHECKING
                                    if (AccountRequestDTO.AccountTypeEnum.CHECKING.name().equalsIgnoreCase(account.getAccountType())) {
                                        account.setMaintenanceFee(new BigDecimal("10.00")); // comisión ejemplo
                                        account.setMonthlyMovementLimit(null);
                                        account.setMovementsThisMonth(0);// sin límite
                                    }

                                    // ⚡ reglas para TERM (puedes definir comisión si aplica)
                                    if (AccountRequestDTO.AccountTypeEnum.TERM.name().equalsIgnoreCase(account.getAccountType())) {
                                        account.setMaintenanceFee(new BigDecimal("0.00"));
                                        account.setOperationDay(LocalDate.now());
                                        account.setMonthlyMovementLimit(null);
                                        account.setMovementsThisMonth(0);
                                    }

                                    return Mono.just(account);
                                });
                    }

                    return Mono.error(new IllegalArgumentException("Tipo de cliente no reconocido: " + customerType));
                });
    }

}