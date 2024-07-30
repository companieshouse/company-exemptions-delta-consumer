package uk.gov.companieshouse.exemptions.delta;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.retrytopic.FixedDelayStrategy;
import org.springframework.messaging.Message;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.delta.ChsDelta;

/**
 * Consumes messages from the configured main Kafka topic.
 */
@Component
public class Consumer implements ConsumerSeekAware {

    private final ServiceRouter router;
    private final MessageFlags messageFlags;

    public Consumer(ServiceRouter router, MessageFlags messageFlags) {
        this.router = router;
        this.messageFlags = messageFlags;
    }

    /**
     * Consume a message from the main Kafka topic.
     *
     * @param message A message containing a {@link ChsDelta delta} containing an
     * {@link uk.gov.companieshouse.api.delta.PscExemptionDelta exemption delta} or an
     * {@link uk.gov.companieshouse.api.delta.PscExemptionDeleteDelta exemption delete delta}.
     */
    @KafkaListener(
            id = "${consumer.group_id}",
            containerFactory = "kafkaListenerContainerFactory",
            topics = "${consumer.topic}",
            groupId = "${consumer.group_id}"
    )
    @RetryableTopic(
            attempts = "${consumer.max_attempts}",
            autoCreateTopics = "false",
            backoff = @Backoff(delayExpression = "${consumer.backoff_delay}"),
            retryTopicSuffix = "-${consumer.group_id}-retry",
            dltTopicSuffix = "-${consumer.group_id}-error",
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            fixedDelayTopicStrategy = FixedDelayStrategy.SINGLE_TOPIC,
            include = RetryableException.class
    )
    public void consume(Message<ChsDelta> message) {
        try {
            router.route(message.getPayload());
        } catch (RetryableException e) {
            messageFlags.setRetryable(true);
            throw e;
        }
    }
}
