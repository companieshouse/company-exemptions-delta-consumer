package uk.gov.companieshouse.exemptions.delta;

import java.math.BigInteger;
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
            String topic = new String(record.headers().lastHeader("kafka_original-topic").value());
            BigInteger partition = new BigInteger(record.headers().lastHeader("kafka_original-partition").value());
            BigInteger offset = new BigInteger(record.headers().lastHeader("kafka_original-offset").value());
            String message = String.format("Invalid message for topic: %s, partition: %d, offset: %d", topic, partition, offset);
            ChsDelta invalidData = new ChsDelta(String.format("{ \"invalid_message\": \"%s\" }", message), 0, "", false);

            ProducerRecord<String, ChsDelta> invalidRecord = new ProducerRecord<>(invalidMessageTopic, record.key(), invalidData);
            LOGGER.info(String.format("Moving record into topic: [%s]\nMessage content: %s", invalidRecord.topic(), message));

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
