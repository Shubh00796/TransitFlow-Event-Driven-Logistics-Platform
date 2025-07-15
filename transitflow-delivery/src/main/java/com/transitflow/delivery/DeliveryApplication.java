package com.transitflow.delivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.transitflow.delivery",
        "com.transitflow.dispatch",         // needed for Shipment and its service
        "com.transitflow.common.configs",   // for shared configs
        "com.transitflow.common.outbox"     // for DomainEventPublisher etc.
})
@EntityScan(basePackages = {
        "com.transitflow.dispatch.domain",  // required for Shipment entity
        "com.transitflow.delivery.domain",
        "com.transitflow.common.outbox"
})
@EnableJpaRepositories(basePackages = {
        "com.transitflow.dispatch.repository.data_access_layer",
        "com.transitflow.delivery.repository",
        "com.transitflow.common.outbox"
})
@EnableTransactionManagement
@EnableScheduling
public class DeliveryApplication {
    public static void main(String[] args) {
        SpringApplication.run(DeliveryApplication.class, args);
    }
}
