package uk.gov.companieshouse.exemptions.delta;

import java.util.Optional;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.delta.ChsDelta;

import java.util.Collections;

/**
 * Consumes messages from the configured error Kafka topic.
 */
@Component
public class ErrorConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorConsumer.class);

    private final KafkaListenerEndpointRegistry registry;
    private final OffsetConstraint offsetConstraint;
    private final ServiceRouter router;
    private final String container;
    private final MessageFlags messageFlags;

    public ErrorConsumer(KafkaListenerEndpointRegistry registry,
            OffsetConstraint offsetConstraint,
            ServiceRouter router,
            @Value("${error_consumer.group_id}") String container,
            MessageFlags messageFlags) {
        this.registry = registry;
        this.offsetConstraint = offsetConstraint;
        this.router = router;
        this.container = container;
        this.messageFlags = messageFlags;
    }

    /**
     * Consume a message from the configured error Kafka topic.<br>
     * <br>
     * On consuming the first message, the consumer retrieves and stores the final offset number of the topic. If this
     * offset number is exceeded, the consumer will be {@link MessageListenerContainer#pause() paused}.<br>
     * <br>
     * An {@link Acknowledgment#acknowledge() acknowledgement} is issued after the meessage is consumed; this is done
     * to prevent the offset immediately after the final offset that was calculated from being processed.
     *
     * @param message        A message containing a {@link ChsDelta delta} containing an
     *                       {@link uk.gov.companieshouse.api.delta.PscExemptionDelta exemption delta} or an
     *                       {@link uk.gov.companieshouse.api.delta.PscExemptionDeleteDelta exemption delete delta}.
     * @param acknowledgment An {@link Acknowledgment acknowledgement handler}.
     */
    @KafkaListener(
            id = "${error_consumer.group_id}",
            containerFactory = "kafkaErrorListenerContainerFactory",
            topics = "${error_consumer.topic}",
            groupId = "${error_consumer.group_id}",
            autoStartup = "${error_consumer.enabled}"
    )
    public void consume(Message<ChsDelta> message, Acknowledgment acknowledgment) {
        KafkaConsumer<?, ?> consumer = Optional.ofNullable((KafkaConsumer<?, ?>) message.getHeaders().get(KafkaHeaders.CONSUMER))
                .orElseThrow(() -> new NonRetryableException("Missing consumer header"));
        String topic = (String) message.getHeaders().get(KafkaHeaders.RECEIVED_TOPIC);
        Integer partition = Optional.ofNullable((Integer) message.getHeaders().get(KafkaHeaders.RECEIVED_PARTITION_ID))
                .orElseThrow(() -> new NonRetryableException("Missing partition header"));
        Long offset = Optional.ofNullable((Long) message.getHeaders().get(KafkaHeaders.OFFSET))
                .orElseThrow(() -> new NonRetryableException("Missing offset header"));
        if (offsetConstraint.getOffsetConstraint() == null) {
            offsetConstraint.setOffsetConstraint(consumer.endOffsets(Collections.singletonList(new TopicPartition(topic, partition))).values().stream().findFirst().orElse(1L) - 1);
        }
        if (offset > offsetConstraint.getOffsetConstraint()) {
            LOGGER.info("Maximum offset exceeded; stopping consumer...");
            Optional.ofNullable(this.registry.getListenerContainer(this.container))
                    .ifPresent(MessageListenerContainer::pause);
        } else {
            try {
                router.route(message.getPayload());
            } catch (RetryableException e) {
                messageFlags.setRetryable(true);
                throw e;
            } finally {
                acknowledgment.acknowledge();
            }
        }
    }
}
