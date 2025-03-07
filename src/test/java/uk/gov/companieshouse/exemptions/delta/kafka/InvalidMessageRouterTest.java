package uk.gov.companieshouse.exemptions.delta.kafka;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.kafka.support.KafkaHeaders.EXCEPTION_MESSAGE;
import static org.springframework.kafka.support.KafkaHeaders.ORIGINAL_OFFSET;
import static org.springframework.kafka.support.KafkaHeaders.ORIGINAL_PARTITION;
import static org.springframework.kafka.support.KafkaHeaders.ORIGINAL_TOPIC;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.delta.ChsDelta;

@ExtendWith(MockitoExtension.class)
class InvalidMessageRouterTest {

    private InvalidMessageRouter invalidMessageRouter;

    @Mock
    private MessageFlags flags;

    @Mock
    private ChsDelta delta;

    @BeforeEach
    void setup() {
        invalidMessageRouter = new InvalidMessageRouter();
        invalidMessageRouter.configure(Map.of("message.flags", flags, "invalid.message.topic", "invalid"));
    }

    @Test
    void testOnSendRoutesMessageToInvalidMessageTopicIfInvalidPayloadExceptionThrown() {
        // given
        ProducerRecord<String, ChsDelta> message = new ProducerRecord<>("main", 0, "key", delta,
                List.of(
                        new RecordHeader(ORIGINAL_TOPIC, "main".getBytes()),
                        new RecordHeader(ORIGINAL_PARTITION, BigInteger.ZERO.toByteArray()),
                        new RecordHeader(ORIGINAL_OFFSET, BigInteger.ONE.toByteArray()),
                        new RecordHeader(EXCEPTION_MESSAGE, "[invalid]".getBytes())));

        ChsDelta invalidData = new ChsDelta(
                "{ \"invalid_message\": \"exception: [ [invalid] ] passed for topic: main, partition: 0, offset: 1\" }",
                0, "", false);
        // when
        ProducerRecord<String, ChsDelta> actual = invalidMessageRouter.onSend(message);

        // then
        verify(flags, times(0)).destroy();
        assertThat(actual, is(equalTo(new ProducerRecord<>("invalid", "key", invalidData))));
    }

    @Test
    void testOnSendRoutesMessageToInvalidMessageTopicIfInvalidPayloadExceptionThrownNoHeaders() {
        // given
        ProducerRecord<String, ChsDelta> message = new ProducerRecord<>("main",  "key", delta);

        ChsDelta invalidData = new ChsDelta(
                "{ \"invalid_message\": \"exception: [ unknown ] passed for topic: unknown, partition: -1, offset: -1\" }",
                0, "", false);
        // when
        ProducerRecord<String, ChsDelta> actual = invalidMessageRouter.onSend(message);

        // then
        verify(flags, times(0)).destroy();
        assertThat(actual, is(equalTo(new ProducerRecord<>("invalid", "key", invalidData))));
    }

    @Test
    void testOnSendRoutesMessageToTargetTopicIfRetryableExceptionThrown() {
        // given
        ProducerRecord<String, ChsDelta> message = new ProducerRecord<>("main", "key", delta);
        when(flags.isRetryable()).thenReturn(true);

        // when
        ProducerRecord<String, ChsDelta> actual = invalidMessageRouter.onSend(message);

        // then
        verify(flags, times(1)).destroy();
        assertThat(actual, is(sameInstance(message)));
    }

}
