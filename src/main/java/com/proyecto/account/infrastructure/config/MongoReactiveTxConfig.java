package com.proyecto.account.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.ReactiveMongoTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

@Configuration
public class MongoReactiveTxConfig {

    @Bean
    public ReactiveMongoTransactionManager reactiveMongoTransactionManager(ReactiveMongoDatabaseFactory factory) {
        return new ReactiveMongoTransactionManager(factory);
    }

    @Bean
    public TransactionalOperator transactionalOperator(ReactiveMongoTransactionManager txManager) {
        return TransactionalOperator.create(txManager);
    }
}
