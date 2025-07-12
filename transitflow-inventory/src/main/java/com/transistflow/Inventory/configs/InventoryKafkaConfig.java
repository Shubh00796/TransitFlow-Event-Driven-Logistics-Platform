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

    public InventoryKafkaConfig(ProducerFactory<String, Object> producerFactory,
                                ConsumerFactory<String, Object> genericConsumerFactory) {
        this.producerFactory = producerFactory;
        this.genericConsumerFactory = genericConsumerFactory;
    }

    @Bean
    public KafkaTransactionManager<String, Object> kafkaTxManager() {
        return new KafkaTransactionManager<>(producerFactory);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderCreatedEvent>
    inventoryKafkaListenerContainerFactory() {

        JsonDeserializer<OrderCreatedEvent> deserializer =
                new JsonDeserializer<>(OrderCreatedEvent.class);
        deserializer.addTrustedPackages("com.transistflow.commans.events");

        ConsumerFactory<String, OrderCreatedEvent> cf =
                new DefaultKafkaConsumerFactory<>(
                        genericConsumerFactory.getConfigurationProperties(),
                        new StringDeserializer(),
                        deserializer
                );

        ConcurrentKafkaListenerContainerFactory<String, OrderCreatedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(cf);

        factory.getContainerProperties()
                .setKafkaAwareTransactionManager(kafkaTxManager());


        factory.getContainerProperties()
                .setAckMode(ContainerProperties.AckMode.BATCH);

        return factory;
    }
}
