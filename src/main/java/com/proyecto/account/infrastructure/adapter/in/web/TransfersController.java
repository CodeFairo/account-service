package com.proyecto.account.infrastructure.adapter.in.web;

import com.proyecto.account.api.TransfersApi;
import com.proyecto.account.application.port.in.TransferUseCase;
import com.proyecto.account.infrastructure.mapper.TransferMapper;
import com.proyecto.account.model.TransferDTO;
import com.proyecto.account.model.TransferRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class TransfersController implements TransfersApi {

    private final TransferUseCase transferUseCase;
    private final TransferMapper transferMapper;

    @Override
    public Mono<ResponseEntity<TransferDTO>> accountsTransferPost(
            Mono<TransferRequestDTO> transferRequestDTO, ServerWebExchange exchange) {

        return transferRequestDTO
                .flatMap(req -> transferUseCase.transfer(
                        req.getFromAccountId(),
                        req.getToAccountId(),
                        BigDecimal.valueOf(req.getAmount()),
                        req.getConcept()
                ))
                .map(transferMapper::toDto)
                .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto));
    }
}
