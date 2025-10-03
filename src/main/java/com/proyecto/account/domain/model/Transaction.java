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
@Document(collection = "transactions")
public class Transaction {
    @Id
    private String id;
    private String accountId;
    private String type;          // "DEPOSIT", "WITHDRAW"
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String concept;
    private LocalDateTime dateTransaction;
}
