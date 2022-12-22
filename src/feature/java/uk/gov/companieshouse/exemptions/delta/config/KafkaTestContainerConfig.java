package uk.gov.companieshouse.exemptions.delta.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

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
import org.springframework.util.backoff.FixedBackOff;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.delta.ChsDelta;
import uk.gov.companieshouse.exemptions.delta.ChsDeltaDeserialiser;
import uk.gov.companieshouse.exemptions.delta.ChsDeltaSerialiser;
import uk.gov.companieshouse.exemptions.delta.FixedDestinationResolver;
import uk.gov.companieshouse.exemptions.delta.InvalidMessageRouter;
import uk.gov.companieshouse.exemptions.delta.MessageFlags;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

import java.util.HashMap;
import java.util.function.Supplier;

public class KafkaTestContainerConfig {

    // Potentially unneeded code because configuration is taken care of through application-integration_consumer_upsert.yml

    /*public KafkaContainer kafkaContainer() {
        KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));
        kafkaContainer.start();
        return kafkaContainer;
    }

    public ConsumerFactory<String, ChsDelta> consumerFactory(KafkaContainer kafkaContainer) {
        return new DefaultKafkaConsumerFactory<>(new HashMap<>() {{
            put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
            put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ChsDeltaDeserialiser.class);
            put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        }}, new StringDeserializer(), new ChsDeltaDeserialiser());
    }

    public ProducerFactory<String, ChsDelta> producerFactory(KafkaContainer kafkaContainer,
            MessageFlags messageFlags) {
        return new DefaultKafkaProducerFactory<>(new HashMap<>() {{
            put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
            put(ProducerConfig.ACKS_CONFIG, "all");
            put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ChsDeltaSerialiser.class);
            put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, InvalidMessageRouter.class.getName());
            put("message.flags", messageFlags);
            put("invalid.message.topic", "company-exemptions-delta-company-exemptions-delta-consumer-invalid");
        }}, new StringSerializer(), new ChsDeltaSerialiser());
    }

    public KafkaTemplate<String, ChsDelta> kafkaTemplate(ProducerFactory<String, ChsDelta> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, ChsDelta>> kafkaListenerContainerFactory(ConsumerFactory<String, ChsDelta> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, ChsDelta> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(1);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }

    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, ChsDelta>> KafkaErrorListenerContainerFactory(CommonErrorHandler errorConsumerErrorHandler, ConsumerFactory<String, ChsDelta> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, ChsDelta> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(1);
        factory.setCommonErrorHandler(errorConsumerErrorHandler);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }

    public CommonErrorHandler errorConsumerErrorHandler(KafkaTemplate<String, ChsDelta> kafkaTemplate, FixedDestinationResolver fixedDestinationResolver) {
        return new DefaultErrorHandler(new DeadLetterPublishingRecoverer(kafkaTemplate, fixedDestinationResolver::resolve), new FixedBackOff(100, 0));
    }

    public Supplier<InternalApiClient> internalApiClientFactory() {
        return ApiSdkManager::getPrivateSDK;
    }*/

}
