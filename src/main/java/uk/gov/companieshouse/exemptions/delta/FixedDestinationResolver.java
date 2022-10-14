package uk.gov.companieshouse.exemptions.delta;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.stereotype.Component;

@Component
public class FixedDestinationResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(FixedDestinationResolver.class);

    private final String topic;
    private final String container;
    private final OffsetConstraint constraint;
    private final KafkaListenerEndpointRegistry registry;

    public FixedDestinationResolver(@Value("${error_consumer.dlt}") String topic,
            @Value("${error_consumer.group_id}") String container,
            OffsetConstraint constraint,
            KafkaListenerEndpointRegistry registry) {
        this.topic = topic;
        this.container = container;
        this.constraint = constraint;
        this.registry = registry;
    }

    public TopicPartition resolve(ConsumerRecord<?, ?> consumerRecord, Exception exception) {
        if (consumerRecord.offset() >= constraint.getOffsetConstraint()) {
            LOGGER.info("Maximum offset exceeded; stopping consumer...");
            this.registry.getListenerContainer(this.container).pause();
        }
        return new TopicPartition(this.topic, 0);
    }
}
