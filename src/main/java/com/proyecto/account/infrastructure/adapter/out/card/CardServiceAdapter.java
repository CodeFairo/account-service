package com.proyecto.account.infrastructure.adapter.out.card;

import com.proyecto.account.application.port.out.CardServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CardServiceAdapter implements CardServicePort {

    private final WebClient.Builder webClientBuilder;

    @Override
    public Mono<Boolean> hasAnyCard(String customerId) {
        return webClientBuilder
                .baseUrl("http://localhost:8083/api/v1/cards")
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder.queryParam("customerId", customerId).build())
                .retrieve()
                .bodyToFlux(CardResponse.class)
                .hasElements(); // Devuelve true si hay al menos una tarjeta
    }

    private record CardResponse(
            String id,
            String cardType,
            String customerId,
            Double creditLimit,
            Double availableCredit,
            Double outstandingBalance,
            String status
    ) {}
}
