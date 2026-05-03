package com.ecommerce.order_service.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import java.util.function.BiFunction;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String,Object>kafkaTemplate){

        DeadLetterPublishingRecoverer recoverer =new DeadLetterPublishingRecoverer(kafkaTemplate, new BiFunction<ConsumerRecord<?, ?>, Exception, TopicPartition>() {
            @Override
            public TopicPartition apply(ConsumerRecord<?, ?> record, Exception e) {
                return new TopicPartition("order-topic-dlq",record.partition());
            }
        });

        FixedBackOff fixedBackOff = new FixedBackOff(2000L,3);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, fixedBackOff);

        // 🔥 THIS IS THE MAIN FIX
        errorHandler.addNotRetryableExceptions(
                org.springframework.web.reactive.function.client.WebClientResponseException.class
        );

        return errorHandler;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String,Object>kafkaListenerContainerFactory(ConsumerFactory<String,Object> consumerFactory,DefaultErrorHandler errorHandler){
        ConcurrentKafkaListenerContainerFactory<String,Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);

        //Enable Retry
        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }
}
