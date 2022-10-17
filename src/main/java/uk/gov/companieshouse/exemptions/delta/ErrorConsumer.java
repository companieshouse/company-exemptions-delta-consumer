package uk.gov.companieshouse.exemptions.delta;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class ErrorConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorConsumer.class);

    private KafkaListenerEndpointRegistry registry;
    private OffsetConstraint offsetConstraint;
    @Value("${error_consumer.group_id}")
    private String container;

    public ErrorConsumer(KafkaListenerEndpointRegistry registry, OffsetConstraint offsetConstraint) {
        this.registry = registry;
        this.offsetConstraint = offsetConstraint;
    }

    @KafkaListener(
            id = "${error_consumer.group_id}",
            containerFactory = "kafkaErrorListenerContainerFactory",
            topics = "${error_consumer.topic}",
            groupId = "${error_consumer.group_id}",
            autoStartup = "${error_consumer.enabled}"
    )
    public void consume(Message<String> message, Acknowledgment acknowledgment) {
        KafkaConsumer<?, ?> consumer = (KafkaConsumer<?, ?>)message.getHeaders().get(KafkaHeaders.CONSUMER);
        String topic = (String)message.getHeaders().get(KafkaHeaders.RECEIVED_TOPIC);
        Integer partition = (Integer)message.getHeaders().get(KafkaHeaders.RECEIVED_PARTITION_ID);
        Long offset = (Long)message.getHeaders().get(KafkaHeaders.OFFSET);
        if (offsetConstraint.getOffsetConstraint() == null) {
            offsetConstraint.setOffsetConstraint(consumer.endOffsets(Collections.singletonList(new TopicPartition(topic, partition))).values().stream().findFirst().orElse(1L) - 1);
        }
        if (offset > offsetConstraint.getOffsetConstraint()) {
            LOGGER.info("Maximum offset exceeded; stopping consumer...");
            this.registry.getListenerContainer(this.container).pause();
        } else {
            try {
                LOGGER.info("Consumed message from: " + topic);
                throw new UnsupportedOperationException("Not implemented");
            } finally {
                acknowledgment.acknowledge();
            }
        }
    }
}
