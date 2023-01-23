package uk.gov.companieshouse.exemptions.delta;

import static org.springframework.kafka.support.KafkaHeaders.EXCEPTION_MESSAGE;
import static org.springframework.kafka.support.KafkaHeaders.ORIGINAL_OFFSET;
import static org.springframework.kafka.support.KafkaHeaders.ORIGINAL_PARTITION;
import static org.springframework.kafka.support.KafkaHeaders.ORIGINAL_TOPIC;

import java.math.BigInteger;
import java.util.Optional;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import uk.gov.companieshouse.delta.ChsDelta;

import java.util.Map;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

public class InvalidMessageRouter implements ProducerInterceptor<String, ChsDelta> {

    private MessageFlags messageFlags;
    private String invalidMessageTopic;
    private Logger LOGGER = LoggerFactory.getLogger(Application.NAMESPACE);

    @Override
    public ProducerRecord<String, ChsDelta> onSend(ProducerRecord<String, ChsDelta> record) {
        if (messageFlags.isRetryable()) {
            messageFlags.destroy();
            return record;
        } else {
            String topic = Optional.ofNullable(record.headers().lastHeader(ORIGINAL_TOPIC))
                    .map(h -> new String(h.value())).orElse("unknown");
            BigInteger partition = Optional.ofNullable(record.headers().lastHeader(ORIGINAL_PARTITION))
                    .map(h -> new BigInteger(h.value())).orElse(BigInteger.valueOf(-1));
            BigInteger offset = Optional.ofNullable(record.headers().lastHeader(ORIGINAL_OFFSET))
                    .map(h -> new BigInteger(h.value())).orElse(BigInteger.valueOf(-1));
            String payload = Optional.ofNullable(record.headers().lastHeader(EXCEPTION_MESSAGE))
                    .map(h -> {
                        String exception = new String(h.value());
                        return exception.substring(exception.indexOf("[")+1, exception.lastIndexOf("]"));
                    }).orElse("unknown");

            ChsDelta invalidData = new ChsDelta(
                    String.format("{ \"invalid_message\": \"payload: [ %s ] passed for topic: %s, partition: %d, offset: %d\" }",
                            payload, topic, partition, offset), 0, "", false);

            ProducerRecord<String, ChsDelta> invalidRecord = new ProducerRecord<>(invalidMessageTopic, record.key(), invalidData);
            LOGGER.info(String.format("Moving record into topic: [%s]\nMessage content: %s", invalidRecord.topic(), invalidData.getData()));

            return invalidRecord;
        }
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
    }

    @Override
    public void close() {
    }

    @Override
    public void configure(Map<String, ?> configs) {
        this.messageFlags = (MessageFlags) configs.get("message.flags");
        this.invalidMessageTopic = (String) configs.get("invalid.message.topic");
    }
}
