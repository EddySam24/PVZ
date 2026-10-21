package com.pvz.game;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.Clock;

@SpringBootApplication
public class PvzApplication {

    public static void main(String[] args) {
        SpringApplication.run(PvzApplication.class, args);
    }

    /** Reloj inyectable: en produccion es el reloj del sistema, en pruebas se reemplaza. */
    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
