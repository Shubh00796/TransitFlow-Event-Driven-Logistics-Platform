package com.transistflow.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
/**
 * Main entry point for the Order Application.
 * <p>
 * Enables Spring Boot auto-configuration, caching, transaction management,
 * JPA auditing, and scheduling.
 */

@SpringBootApplication
@EnableCaching
@EnableTransactionManagement
@EnableJpaAuditing
@EnableScheduling
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}
