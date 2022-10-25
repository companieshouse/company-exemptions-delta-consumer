package uk.gov.companieshouse.exemptions.delta;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.delta.ChsDelta;

import java.util.Collections;

@Component
public class ErrorConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorConsumer.class);

    private final KafkaListenerEndpointRegistry registry;
    private final OffsetConstraint offsetConstraint;
    private final ServiceRouter router;
    private final String container;

    public ErrorConsumer(KafkaListenerEndpointRegistry registry,
                         OffsetConstraint offsetConstraint,
                         ServiceRouter router,
                         @Value("${error_consumer.group_id}") String container) {
        this.registry = registry;
        this.offsetConstraint = offsetConstraint;
        this.router = router;
        this.container = container;
    }

    @KafkaListener(
            id = "${error_consumer.group_id}",
            containerFactory = "kafkaErrorListenerContainerFactory",
            topics = "${error_consumer.topic}",
            groupId = "${error_consumer.group_id}",
            autoStartup = "${error_consumer.enabled}"
    )
    public void consume(Message<ChsDelta> message, Acknowledgment acknowledgment) {
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
                router.route(message);
            } finally {
                acknowledgment.acknowledge();
            }
        }
    }
}
