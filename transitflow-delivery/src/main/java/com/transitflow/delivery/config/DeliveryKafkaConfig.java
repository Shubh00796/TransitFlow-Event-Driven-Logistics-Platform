package com.transitflow.delivery.config;


import com.transitflow.common.events.ShipmentDispatchedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class DeliveryKafkaConfig {

    // ✅ Consumer Factory for ShipmentDispatchedEvent
    @Bean
    public ConsumerFactory<String, ShipmentDispatchedEvent> shipmentDispatchedEventConsumerFactory() {
        JsonDeserializer<ShipmentDispatchedEvent> deserializer =
                new JsonDeserializer<>(ShipmentDispatchedEvent.class, false);
        deserializer.addTrustedPackages("*");

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "delivery-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer.getClass());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false); // manual commit for reliability
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 10);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                deserializer
        );
    }

    // ✅ KafkaListener Factory for ShipmentDispatchedEvent
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ShipmentDispatchedEvent>
    shipmentDispatchedEventKafkaListenerContainerFactory(
            ConsumerFactory<String, ShipmentDispatchedEvent> shipmentDispatchedEventConsumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, ShipmentDispatchedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(shipmentDispatchedEventConsumerFactory);
        factory.setConcurrency(2); // parallel processing
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.getContainerProperties().setPollTimeout(3000);

        // Error handling
        factory.setCommonErrorHandler(new org.springframework.kafka.listener.DefaultErrorHandler());

        return factory;
    }
}