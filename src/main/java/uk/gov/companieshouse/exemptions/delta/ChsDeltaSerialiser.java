package uk.gov.companieshouse.exemptions.delta;

import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.reflect.ReflectDatumWriter;
import org.apache.kafka.common.serialization.Serializer;
import uk.gov.companieshouse.delta.ChsDelta;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ChsDeltaSerialiser implements Serializer<ChsDelta> {

    @Override
    public byte[] serialize(String topic, ChsDelta data) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Encoder encoder = EncoderFactory.get().directBinaryEncoder(outputStream, null);
        DatumWriter<ChsDelta> writer = new ReflectDatumWriter<>(ChsDelta.class);
        try {
            writer.write(data, encoder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return outputStream.toByteArray();
    }
}
