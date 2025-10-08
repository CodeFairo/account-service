package com.proyecto.account.application.policy;

import com.proyecto.account.application.port.out.TransactionRepositoryPort;
import com.proyecto.account.domain.model.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class AccountTransactionValidator {

    private final TransactionRepositoryPort txRepo;

    /**
     * txType: "DEPOSIT" | "WITHDRAW"
     */
    public Mono<Account> validate(Account acc, String txType) {
        String type = acc.getAccountType();

        if ("SAVINGS".equalsIgnoreCase(type)) {
            if (acc.getMovementsThisMonth() >= acc.getMonthlyMovementLimit()) {
                return Mono.error(new IllegalStateException(
                        "La cuenta de ahorro ha superado el límite mensual de movimientos"));
            }
            return Mono.just(acc);
        }

        if ("CHECKING".equalsIgnoreCase(type)) {
            return Mono.just(acc);
        }

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

            // (opcional) Si quieres forzar una sola operación ese día descomenta:
            // return txRepo.findByAccountId(acc.getId())
            //         .filter(tx -> tx.getDateTransaction().toLocalDate().equals(LocalDate.now()))
            //         .hasElements()
            //         .flatMap(exists -> exists
            //                 ? Mono.error(new IllegalStateException("La cuenta a plazo fijo solo permite un movimiento por día"))
            //                 : Mono.just(acc));

            return Mono.just(acc);
        }

        return Mono.error(new IllegalStateException("Tipo de cuenta no soportado: " + type));
    }
}
