package uk.gov.companieshouse.exemptions.delta;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.util.backoff.FixedBackOff;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.delta.ChsDelta;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

import java.util.HashMap;
import java.util.function.Supplier;

@Configuration
@EnableKafka
public class Config {

    @Bean
    public ConsumerFactory<String, ChsDelta> consumerFactory(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        return new DefaultKafkaConsumerFactory<>(new HashMap<>() {{
            put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
            put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
            put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
            put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, ChsDeltaDeserialiser.class);
            put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        }}, new StringDeserializer(), new ErrorHandlingDeserializer<>(new ChsDeltaDeserialiser()));
    }

    @Bean
    public ProducerFactory<String, ChsDelta> producerFactory(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers,
                                                             MessageFlags messageFlags,
                                                             @Value("${invalid_message_topic}") String invalidMessageTopic) {
        return new DefaultKafkaProducerFactory<>(new HashMap<>() {{
            put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            put(ProducerConfig.ACKS_CONFIG, "all");
            put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ChsDeltaSerialiser.class);
            put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, InvalidMessageRouter.class.getName());
            put("message.flags", messageFlags);
            put("invalid.message.topic", invalidMessageTopic);
        }}, new StringSerializer(), new ChsDeltaSerialiser());
    }

    @Bean
    public KafkaTemplate<String, ChsDelta> kafkaTemplate(ProducerFactory<String, ChsDelta> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, ChsDelta>> kafkaListenerContainerFactory(ConsumerFactory<String, ChsDelta> consumerFactory, @Value("${consumer.concurrency}") Integer concurrency) {
        ConcurrentKafkaListenerContainerFactory<String, ChsDelta> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(concurrency);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, ChsDelta>> kafkaErrorListenerContainerFactory(CommonErrorHandler errorConsumerErrorHandler, ConsumerFactory<String, ChsDelta> consumerFactory, @Value("${error_consumer.concurrency}") Integer concurrency) {
        ConcurrentKafkaListenerContainerFactory<String, ChsDelta> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(concurrency);
        factory.setCommonErrorHandler(errorConsumerErrorHandler);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }

    @Bean
    public CommonErrorHandler errorConsumerErrorHandler(KafkaTemplate<String, ChsDelta> kafkaTemplate, FixedDestinationResolver fixedDestinationResolver) {
        return new DefaultErrorHandler(new DeadLetterPublishingRecoverer(kafkaTemplate, fixedDestinationResolver::resolve), new FixedBackOff(100, 0));
    }

    @Bean
    public Supplier<InternalApiClient> internalApiClientFactory() {
        return ApiSdkManager::getPrivateSDK;
    }

    @Bean
    public Logger logger() {
        return LoggerFactory.getLogger(Application.NAMESPACE);
    }
}
