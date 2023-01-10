package uk.gov.companieshouse.exemptions.delta;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
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
            ObjectMapper mapper = new ObjectMapper();
            String dataMessage = "";
            try {
                byte[] stringBytes = mapper.writeValueAsBytes(record.value());
                dataMessage = Base64.getEncoder().encodeToString(stringBytes);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            LOGGER.info("HELLO WORLD1");
            //TODO capture as much metadata as possible.
            ChsDelta invalidData = new ChsDelta(dataMessage, 0, "context_id", false);
            ProducerRecord<String, ChsDelta> invalidRecord = new ProducerRecord<>(invalidMessageTopic, record.key(), invalidData);
            LOGGER.info(String.format("Moving record into topic: [%s]\nMessage content: %s",
                    invalidRecord.topic(), dataMessage));
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
