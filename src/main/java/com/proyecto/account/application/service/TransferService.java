package com.proyecto.account.application.service;

import com.proyecto.account.application.port.in.TransferUseCase;
import com.proyecto.account.application.port.out.TransferRepositoryPort;
import com.proyecto.account.domain.model.Transaction;
import com.proyecto.account.domain.model.Transfer;
import com.proyecto.account.domain.util.OperationNumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransferService implements TransferUseCase {

    private final TransactionOperationHelper transactionHelper;  // 🔹 usamos el helper directamente
    private final TransferRepositoryPort transferRepo;
    private final TransactionalOperator txOperator;

    @Override
    public Mono<Transfer> transfer(String fromAccountId, String toAccountId, BigDecimal amount, String concept) {

        String op = OperationNumberGenerator.generate();

        // Ejecutar todo dentro de una transacción reactiva de Mongo (rollback automático)
        return txOperator.transactional(
                transactionHelper.withdrawWithOp(fromAccountId, amount, concept, op)
                        .zipWhen(
                                withdrawTx -> transactionHelper.depositWithOp(toAccountId, amount, concept, op),
                                TxPair::new
                        )
                        .flatMap(pair -> {
                            Transaction withdrawTx = pair.withdrawTx();
                            Transaction depositTx = pair.depositTx();

                            Transfer transfer = Transfer.builder()
                                    .operationNumber(op)
                                    .fromAccountId(fromAccountId)
                                    .toAccountId(toAccountId)
                                    .amount(amount)
                                    .concept(concept)
                                    .dateTransfer(LocalDateTime.now())
                                    .withdrawTransactionId(withdrawTx.getId())
                                    .depositTransactionId(depositTx.getId())
                                    .build();

                            return transferRepo.save(transfer);
                        })
        );
    }

    private record TxPair(Transaction withdrawTx, Transaction depositTx) {}
}
