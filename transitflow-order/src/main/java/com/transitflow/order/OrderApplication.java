package com.transitflow.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
/**
 * Main entry point for the Order Application.
 * <p>
 * Enables Spring Boot auto-configuration, caching, transaction management,
 * JPA auditing, and scheduling.
 */


@EnableCaching
@EnableTransactionManagement
@EnableJpaAuditing
@EnableScheduling
@SpringBootApplication(scanBasePackages = {
        "com.transistflow.order",
        "com.transistflow.commans"
})
@EntityScan(basePackages = {
        "com.transistflow.order.domain",
        "com.transistflow.commans.outbox"   // 🔥 include all entity packages
})
@EnableJpaRepositories(basePackages = {
        "com.transistflow.order.reposiotries",
        "com.transistflow.commans.outbox"
})
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}
