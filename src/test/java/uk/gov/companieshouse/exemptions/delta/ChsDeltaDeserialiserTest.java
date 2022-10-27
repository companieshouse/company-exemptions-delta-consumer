package uk.gov.companieshouse.exemptions.delta;

import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.reflect.ReflectDatumWriter;
import org.apache.avro.specific.SpecificDatumWriter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.delta.ChsDelta;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

class ChsDeltaDeserialiserTest {

    @Test
    @DisplayName("Deserialise a ChsDelta serialised as Avro")
    void testDeserialiseDelta() throws IOException {
        // given
        ChsDelta delta = new ChsDelta("{}", 0, "context_id", false);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Encoder encoder = EncoderFactory.get().directBinaryEncoder(outputStream, null);
        DatumWriter<ChsDelta> writer = new ReflectDatumWriter<>(ChsDelta.class);
        writer.write(delta, encoder);
        ChsDeltaDeserialiser deserialiser = new ChsDeltaDeserialiser();

        // when
        ChsDelta actual = deserialiser.deserialize("topic", outputStream.toByteArray());

        // then
        assertThat(actual, is(equalTo(delta)));
    }

    @Test
    @DisplayName("Return null if an IOException is thrown when deserialising a message")
    void testDeserialiseDataReturnsNullIfIOExceptionThrown() throws IOException {
        // given
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Encoder encoder = EncoderFactory.get().directBinaryEncoder(outputStream, null);
        DatumWriter<String> writer = new SpecificDatumWriter<>(String.class);
        writer.write("invalid", encoder);
        ChsDeltaDeserialiser deserialiser = new ChsDeltaDeserialiser();

        // when
        ChsDelta actual = deserialiser.deserialize("topic", outputStream.toByteArray());

        // then
        assertThat(actual, is(nullValue()));
    }

    @Test
    @DisplayName("Return null if an AvroException is thrown when deserialising a message")
    void testDeserialiseDataReturnsNullIfAvroRuntimeExceptionThrown() {
        // given
        ChsDeltaDeserialiser deserialiser = new ChsDeltaDeserialiser();

        // when
        ChsDelta actual = deserialiser.deserialize("topic", "invalid".getBytes(StandardCharsets.UTF_8));

        // then
        assertThat(actual, is(nullValue()));
    }
}
