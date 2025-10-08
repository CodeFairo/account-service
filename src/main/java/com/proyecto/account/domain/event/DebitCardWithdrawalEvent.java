package com.proyecto.account.domain.event;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DebitCardWithdrawalEvent {
    private String cardId;
    private String accountId;
    private String customerId;
    private BigDecimal amount;
    private String description;
    private LocalDateTime timestamp;
}
