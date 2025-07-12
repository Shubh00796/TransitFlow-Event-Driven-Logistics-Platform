package com.transistflow.Inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.transistflow.Inventory",             // local inventory-service beans
        "com.transistflow.commans"               // shared common module (includes configs, events, outbox, etc.)
})
@EnableJpaRepositories(basePackages = {
        "com.transistflow.Inventory.repositories",
        "com.transistflow.commans.outbox" // 💥 This is key
})
@EntityScan(basePackages = {
        "com.transistflow.Inventory.domain",
        "com.transistflow.commans.outbox"
})
@EnableTransactionManagement
@EnableScheduling
@EnableCaching
@EnableJpaAuditing
public class InventoryApplication {
    public static void main(String[] args) {
        SpringApplication.run(InventoryApplication.class, args);
    }
}
