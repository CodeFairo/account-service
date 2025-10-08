package com.proyecto.account.domain.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class OperationNumberGenerator {

    private static final DateTimeFormatter BASE = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private OperationNumberGenerator() {}

    public static String generate() {
        // base sin milis
        String base = LocalDateTime.now().format(BASE);
        // dos dígitos de milisegundos (centésimas)
        int hundredths = (int)((System.currentTimeMillis() % 1000) / 10);
        return base + String.format("%02d", hundredths);
    }
}
