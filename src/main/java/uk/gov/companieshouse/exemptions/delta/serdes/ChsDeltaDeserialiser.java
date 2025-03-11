package uk.gov.companieshouse.exemptions.delta.serdes;

import static uk.gov.companieshouse.exemptions.delta.CompanyExemptionsDeltaConsumerApplication.NAMESPACE;

import java.io.IOException;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.reflect.ReflectDatumReader;
import org.apache.kafka.common.serialization.Deserializer;
import uk.gov.companieshouse.delta.ChsDelta;
import uk.gov.companieshouse.exemptions.delta.exception.InvalidPayloadException;
import uk.gov.companieshouse.exemptions.delta.logging.DataMapHolder;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

/**
 * Transforms an incoming delta from Avro to a {@link ChsDelta}.
 */
public class ChsDeltaDeserialiser implements Deserializer<ChsDelta> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NAMESPACE);

    /**
     * Deserialise a {@link ChsDelta} from an Avro representation of the delta as a byte array.
     *
     * @param topic The topic from which the message was consumed.
     * @param data An Avro representation of the delta as a byte array.
     * @return A {@link ChsDelta} deserialised from Avro.
     */
    @Override
    public ChsDelta deserialize(String topic, byte[] data) {
        try {
            Decoder decoder = DecoderFactory.get().binaryDecoder(data, null);
            DatumReader<ChsDelta> reader = new ReflectDatumReader<>(ChsDelta.class);
            return reader.read(null, decoder);
        } catch (IOException | AvroRuntimeException e) {
            String payload = new String(data);
            LOGGER.error("Error deserialising message payload: [%s]".formatted(payload), e, DataMapHolder.getLogMap());
            throw new InvalidPayloadException(String.format("Invalid payload: [%s] was provided.", payload), e);
        }
    }
}
