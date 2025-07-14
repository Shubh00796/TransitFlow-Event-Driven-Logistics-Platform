package com.transitflow.dispatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main entry point for the Dispatch Service.
 * Enables component scanning, transaction management, JPA auditing,
 * and scheduling to support outbox pattern and Kafka events.
 */
@SpringBootApplication
@ComponentScan(basePackages = {
        "com.transitflow.dispatch",
        "com.transitflow.common",           // Fixed: consistent package name
        "com.transitflow.order.configs"
})
@EnableJpaRepositories(basePackages = {
        "com.transitflow.dispatch.repository",
        "com.transitflow.common.outbox"
})
@EntityScan(basePackages = {
        "com.transitflow.dispatch.domain",
        "com.transitflow.common.outbox"
})
@EnableTransactionManagement
@EnableScheduling
@EnableCaching
@EnableJpaAuditing
public class DispatchApplication {

    public static void main(String[] args) {
        System.out.println("=== BUILDING CLEAN FILE ===");
        SpringApplication.run(DispatchApplication.class, args);
    }
}