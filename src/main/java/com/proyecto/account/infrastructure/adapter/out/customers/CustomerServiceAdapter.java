package com.proyecto.account.infrastructure.adapter.out.customers;

import com.proyecto.account.application.port.out.CustomerServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CustomerServiceAdapter implements CustomerServicePort {

    private final WebClient.Builder webClientBuilder;

    @Override
    public Mono<String> getCustomerType(String customerId) {
        return webClientBuilder
                .baseUrl("http://localhost:8080/api/v1/customers")
                .build()
                .get()
                .uri("/{id}", customerId)
                .retrieve()
                .bodyToMono(CustomerResponse.class)
                .map(CustomerResponse::customerType);
    }


    private record CustomerResponse(
            String id,
            String customerType
    )
    {}
}
