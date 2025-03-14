package uk.gov.companieshouse.exemptions.delta.kafka;

import static uk.gov.companieshouse.exemptions.delta.kafka.KafkaUtils.COMPANY_EXEMPTIONS_DELTA_ERROR_TOPIC;
import static uk.gov.companieshouse.exemptions.delta.kafka.KafkaUtils.COMPANY_EXEMPTIONS_DELTA_INVALID_TOPIC;
import static uk.gov.companieshouse.exemptions.delta.kafka.KafkaUtils.COMPANY_EXEMPTIONS_DELTA_RETRY_TOPIC;
import static uk.gov.companieshouse.exemptions.delta.kafka.KafkaUtils.COMPANY_EXEMPTIONS_DELTA_TOPIC;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestKafkaConfig {

    @Bean
    KafkaConsumer<String, byte[]> testConsumer(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        KafkaConsumer<String, byte[]> consumer = new KafkaConsumer<>(new HashMap<>() {private static final long serialVersionUID = 7449366286646831115L;

        {
            put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
            put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
            put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        }}, new StringDeserializer(), new ByteArrayDeserializer());

        consumer.subscribe(List.of(
                COMPANY_EXEMPTIONS_DELTA_TOPIC,
                COMPANY_EXEMPTIONS_DELTA_INVALID_TOPIC,
                COMPANY_EXEMPTIONS_DELTA_RETRY_TOPIC,
                COMPANY_EXEMPTIONS_DELTA_ERROR_TOPIC
        ));

        return consumer;
    }

    @Bean
    KafkaProducer<String, byte[]> testProducer(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        return new KafkaProducer<>(new HashMap<>() {private static final long serialVersionUID = 6409518428577007424L;

        {
            put(ProducerConfig.ACKS_CONFIG, "all");
            put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        }}, new StringSerializer(), new ByteArraySerializer());
    }
}