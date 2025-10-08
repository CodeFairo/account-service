package com.proyecto.account.infrastructure.adapter.out.credit;

import com.proyecto.account.application.port.out.CreditServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CreditServiceAdapter implements CreditServicePort {

    private final WebClient.Builder webClientBuilder;

    @Override
    public Mono<Boolean> hasAnyCredit(String customerId) {
        return webClientBuilder
                .baseUrl("http://localhost:8082/api/v1/credits")
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder.queryParam("customerId", customerId).build())
                .retrieve()
                .bodyToFlux(CreditResponse.class)
                .filter(credit -> "DEFEATED".equalsIgnoreCase(credit.status())) // solo créditos activos
                .hasElements(); // true si hay al menos un crédito activo
    }

    private record CreditResponse(
            String id,
            String creditType,
            String customerId,
            Double principal,
            Double outstandingBalance,
            String status
    ) {}
}
