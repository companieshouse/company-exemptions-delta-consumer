package uk.gov.companieshouse.exemptions.delta;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

import java.io.IOException;
import org.apache.avro.io.DatumWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.delta.ChsDelta;

@ExtendWith(MockitoExtension.class)
public class ChsDeltaSerialiserTest {

    @Mock
    private DatumWriter<ChsDelta> writer;

    @Test
    void testSerialiseChsDelta() {
        // given
        ChsDelta delta = new ChsDelta("{}", 0, "context_id", false);
        try (ChsDeltaSerialiser serialiser = new ChsDeltaSerialiser()) {
            // when
            byte[] actual = serialiser.serialize("topic", delta);

            // then
            assertThat(actual, is(notNullValue()));
        }
    }

    @Test
    void testThrowNonRetryableExceptionIfIOExceptionThrown() throws IOException {
        // given
        ChsDelta delta = new ChsDelta("{}", 0, "context_id", false);
        ChsDeltaSerialiser serialiser = spy(new ChsDeltaSerialiser());
        doReturn(writer).when(serialiser).getDatumWriter();
        doThrow(IOException.class).when(writer).write(any(), any());

        // when
        Executable actual = () -> serialiser.serialize("topic", delta);

        // then
        NonRetryableException exception = assertThrows(NonRetryableException.class, actual);
        assertThat(exception.getMessage(), is(equalTo("Error serialising delta")));
        assertThat(exception.getCause(), is(instanceOf(IOException.class)));
    }
}
