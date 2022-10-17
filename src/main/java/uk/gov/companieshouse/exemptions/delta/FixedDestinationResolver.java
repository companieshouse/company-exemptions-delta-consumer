package uk.gov.companieshouse.exemptions.delta;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FixedDestinationResolver {

    private final String topic;

    public FixedDestinationResolver(@Value("${error_consumer.dlt}") String topic) {
        this.topic = topic;
    }

    public TopicPartition resolve(ConsumerRecord<?, ?> consumerRecord, Exception exception) {
        return new TopicPartition(this.topic, 0);
    }
}
