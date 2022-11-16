package uk.gov.companieshouse.exemptions.delta;

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
            ProducerRecord invalidRecord = new ProducerRecord<>(invalidMessageTopic, record.key(), record.value());
            LOGGER.info(String.format("Moving record into topic: [%s]\nMessage content: %s",
                    invalidRecord.topic(), invalidRecord.value()));
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
