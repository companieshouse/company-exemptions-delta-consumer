package uk.gov.companieshouse.exemptions.delta.config;

import java.util.Map;
import java.util.function.Supplier;
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
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.http.ApiKeyHttpClient;
import uk.gov.companieshouse.delta.ChsDelta;
import uk.gov.companieshouse.exemptions.delta.kafka.InvalidMessageRouter;
import uk.gov.companieshouse.exemptions.delta.kafka.MessageFlags;
import uk.gov.companieshouse.exemptions.delta.serdes.ChsDeltaDeserialiser;
import uk.gov.companieshouse.exemptions.delta.serdes.ChsDeltaSerialiser;

@Configuration
@EnableKafka
public class ApplicationConfig {

    @Bean
    public ConsumerFactory<String, ChsDelta> consumerFactory(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        return new DefaultKafkaConsumerFactory<>(Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class,
            ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class,
            ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, ChsDeltaDeserialiser.class,
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false")
        , new StringDeserializer(), new ErrorHandlingDeserializer<>(new ChsDeltaDeserialiser()));
    }

    @Bean
    public ProducerFactory<String, ChsDelta> producerFactory(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers,
                                                             MessageFlags messageFlags,
                                                             @Value("${invalid_message_topic}") String invalidMessageTopic) {
        return new DefaultKafkaProducerFactory<>(Map.of(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
            ProducerConfig.ACKS_CONFIG, "all",
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ChsDeltaSerialiser.class,
            ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, InvalidMessageRouter.class.getName(),
            "message.flags", messageFlags,
            "invalid.message.topic", invalidMessageTopic)
        , new StringSerializer(), new ChsDeltaSerialiser());
    }

    @Bean
    public KafkaTemplate<String, ChsDelta> kafkaTemplate(ProducerFactory<String, ChsDelta> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, ChsDelta>> kafkaListenerContainerFactory(
            ConsumerFactory<String, ChsDelta> consumerFactory,
            @Value("${consumer.concurrency}") Integer concurrency) {
        ConcurrentKafkaListenerContainerFactory<String, ChsDelta> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(concurrency);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }

    @Bean
    Supplier<InternalApiClient> internalApiClientSupplier(
            @Value("${api.api-key}") String apiKey,
            @Value("${api.api-url}") String apiUrl) {
        return () -> {
            InternalApiClient internalApiClient = new InternalApiClient(new ApiKeyHttpClient(
                    apiKey));
            internalApiClient.setBasePath(apiUrl);
            return internalApiClient;
        };
    }
}