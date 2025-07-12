package com.transistflow.order.configs;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class TransactionConfig {

    /**
     * ✅ Default JPA Transaction Manager
     * This is used when @Transactional is called without a qualifier.
     */
    @Bean(name = "jpaTransactionManager")
    public JpaTransactionManager jpaTransactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    /**
     * ✅ Kafka Transaction Manager
     * This handles Kafka producer transactional support.
     */
    @Bean(name = "kafkaTransactionManager")
    public KafkaTransactionManager<String, Object> kafkaTransactionManager(
            ProducerFactory<String, Object> producerFactory) {
        return new KafkaTransactionManager<>(producerFactory);
    }

    /**
     * ✅ Chained Transaction Manager (Primary)
     * Combines JPA and Kafka transaction managers.
     * This becomes the default used when @Transactional is unqualified.
     */
    @Primary
    @Bean(name = "transactionManager")
    public ChainedTransactionManager transactionManager(
            JpaTransactionManager jpaTransactionManager,
            KafkaTransactionManager<String, Object> kafkaTransactionManager) {
        return new ChainedTransactionManager(jpaTransactionManager, kafkaTransactionManager);
    }
}
