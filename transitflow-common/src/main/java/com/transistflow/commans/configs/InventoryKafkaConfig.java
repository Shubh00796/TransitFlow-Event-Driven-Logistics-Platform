package com.transistflow.commans.configs;

import com.transistflow.commans.events.OrderCreatedEvent;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;

@Configuration
@EnableKafka
public class InventoryKafkaConfig {

    private final ConsumerFactory<String, Object> baseConsumerFactory;

    public InventoryKafkaConfig(ConsumerFactory<String, Object> baseConsumerFactory) {
        this.baseConsumerFactory = baseConsumerFactory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderCreatedEvent>
    inventoryKafkaListenerContainerFactory() {

        // Specialize the deserializer for OrderCreatedEvent
        JsonDeserializer<OrderCreatedEvent> deserializer = new JsonDeserializer<>(OrderCreatedEvent.class);
        deserializer.addTrustedPackages("com.transistflow.commans.events");

        // Build a consumer factory specialized for OrderCreatedEvent
        Map<String, Object> props = baseConsumerFactory.getConfigurationProperties();
        ConsumerFactory<String, OrderCreatedEvent> specializedFactory = new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                deserializer
        );

        // Build the listener container factory
        ConcurrentKafkaListenerContainerFactory<String, OrderCreatedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(specializedFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);

        // Optional (uncomment if using transactional consumption):
        // factory.getContainerProperties().setTransactionManager(kafkaTransactionManager());

        return factory;
    }
}
