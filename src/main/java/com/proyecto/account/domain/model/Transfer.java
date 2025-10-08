package com.proyecto.account.domain.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "transfers")
public class Transfer {
    @Id
    private String id;

    private String operationNumber;        // mismo número para toda la operación
    private String fromAccountId;
    private String toAccountId;
    private BigDecimal amount;
    private String concept;
    private LocalDateTime dateTransfer;

    private String withdrawTransactionId;  // id tx retiro
    private String depositTransactionId;   // id tx depósito
}
