package com.transistflow.Inventory.configs;

import com.transistflow.commans.events.OrderCreatedEvent;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableKafka
public class InventoryKafkaConfig {

    private final ProducerFactory<String, Object> producerFactory;
    private final ConsumerFactory<String, Object> genericConsumerFactory;
    private final PlatformTransactionManager jpaTxManager;

    public InventoryKafkaConfig(ProducerFactory<String, Object> producerFactory,
                                ConsumerFactory<String, Object> genericConsumerFactory,
                                PlatformTransactionManager jpaTxManager) {
        this.producerFactory = producerFactory;
        this.genericConsumerFactory = genericConsumerFactory;
        this.jpaTxManager = jpaTxManager;
    }

    @Bean
    public KafkaTransactionManager<String, Object> kafkaTxManager() {
        return new KafkaTransactionManager<>(producerFactory);
    }

    @Bean
    public ChainedTransactionManager inventoryChainedTxManager() {
        return new ChainedTransactionManager(kafkaTxManager(), jpaTxManager);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderCreatedEvent>
    inventoryKafkaListenerContainerFactory() {

        JsonDeserializer<OrderCreatedEvent> deserializer = new JsonDeserializer<>(OrderCreatedEvent.class);
        deserializer.addTrustedPackages("com.transistflow.commans.events");

        ConsumerFactory<String, OrderCreatedEvent> cf = new DefaultKafkaConsumerFactory<>(
                genericConsumerFactory.getConfigurationProperties(),
                new StringDeserializer(),
                deserializer
        );

        ConcurrentKafkaListenerContainerFactory<String, OrderCreatedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(cf);

        // Correct non-deprecated transaction manager setup
        factory.setTransactionManager(inventoryChainedTxManager());

        // Optional: for better batching semantics
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);

        return factory;
    }
}
