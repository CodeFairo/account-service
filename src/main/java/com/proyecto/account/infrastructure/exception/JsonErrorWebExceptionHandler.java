package com.proyecto.account.infrastructure.exception;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;

import java.util.Map;

@Component
@Order(-2) // muy importante para que tenga prioridad sobre el DefaultErrorWebExceptionHandler
public class JsonErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

    public JsonErrorWebExceptionHandler(ErrorAttributes errorAttributes,
                                        ApplicationContext applicationContext,
                                        ServerCodecConfigurer codecConfigurer) {
        super(errorAttributes, new WebProperties.Resources(), applicationContext);
        super.setMessageWriters(codecConfigurer.getWriters());
        super.setMessageReaders(codecConfigurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), request -> {
            Map<String, Object> errorProperties = getErrorAttributes(request, ErrorAttributeOptions.defaults());

            return ServerResponse.status((int) errorProperties.get("status"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(errorProperties);
        });
    }
}
