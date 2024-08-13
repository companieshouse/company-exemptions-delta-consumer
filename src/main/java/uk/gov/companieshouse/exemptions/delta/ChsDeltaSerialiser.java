package uk.gov.companieshouse.exemptions.delta;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.reflect.ReflectDatumWriter;
import org.apache.kafka.common.serialization.Serializer;
import uk.gov.companieshouse.delta.ChsDelta;

/**
 * Serialises a {@link ChsDelta delta} as a byte array (e.g. if the message has to be republished to the retry/error topics).
 */
public class ChsDeltaSerialiser implements Serializer<ChsDelta> {

    /**
     * Serialise a {@link ChsDelta delta} as a byte array.
     *
     * @param topic The topic to which the message will be published.
     * @param data A {@link ChsDelta delta} that will be serialised.
     * @return A byte array representing a serialised delta.
     */
    @Override
    public byte[] serialize(String topic, ChsDelta data) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Encoder encoder = EncoderFactory.get().directBinaryEncoder(outputStream, null);
        DatumWriter<ChsDelta> writer = getDatumWriter();
        try {
            writer.write(data, encoder);
        } catch (IOException e) {
            throw new NonRetryableException("Error serialising delta", e);
        }
        return outputStream.toByteArray();
    }

    DatumWriter<ChsDelta> getDatumWriter() {
        return new ReflectDatumWriter<>(ChsDelta.class);
    }
}
