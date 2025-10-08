package com.proyecto.account.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "accounts")
public class Account {

    @Id
    private String id;

    private String accountType;

    private String status;

    private BigDecimal balance;

    private List<String> customerIds;
    // Reglas específicas para ahorro
    private Integer monthlyMovementLimit;   // movimeinto por mes
    private Integer movementsThisMonth;     // contador actual
    private BigDecimal maintenanceFee;      // mantenimento
    private LocalDate operationDay;      // dia unico de operacion para TERM
    private BigDecimal minimumAverageBalance;
}
