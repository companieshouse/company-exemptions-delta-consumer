package uk.gov.companieshouse.exemptions.delta.kafka.consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.companieshouse.exemptions.delta.kafka.KafkaUtils.COMPANY_EXEMPTIONS_DELTA_ERROR_TOPIC;
import static uk.gov.companieshouse.exemptions.delta.kafka.KafkaUtils.COMPANY_EXEMPTIONS_DELTA_INVALID_TOPIC;
import static uk.gov.companieshouse.exemptions.delta.kafka.KafkaUtils.COMPANY_EXEMPTIONS_DELTA_RETRY_TOPIC;
import static uk.gov.companieshouse.exemptions.delta.kafka.KafkaUtils.COMPANY_EXEMPTIONS_DELTA_TOPIC;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.util.concurrent.Future;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.reflect.ReflectDatumWriter;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import uk.gov.companieshouse.exemptions.delta.kafka.AbstractKafkaTest;
import uk.gov.companieshouse.exemptions.delta.kafka.KafkaUtils;

@SpringBootTest
@WireMockTest(httpPort = 8888)
class ConsumerInvalidTopicTest extends AbstractKafkaTest {

    @Autowired
    private KafkaConsumer<String, byte[]> testConsumer;

    @Autowired
    private KafkaProducer<String, byte[]> testProducer;

    @Autowired
    private ConsumerAspect consumerAspect;

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", confluentKafkaContainer::getBootstrapServers);
        registry.add("steps", () -> 1);
    }

    @BeforeEach
    public void setup() {
        consumerAspect.resetLatch();
        testConsumer.poll(Duration.ofMillis(1000));
    }

    @Test
    void testPublishToInvalidMessageTopicIfInvalidDataDeserialised() throws Exception {
        //given
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Encoder encoder = EncoderFactory.get().directBinaryEncoder(outputStream, null);
        DatumWriter<String> writer = new ReflectDatumWriter<>(String.class);
        writer.write("hello", encoder);

        //when
        Future<RecordMetadata> future = testProducer.send(new ProducerRecord<>(COMPANY_EXEMPTIONS_DELTA_TOPIC, 0, System.currentTimeMillis(), "key", outputStream.toByteArray()));
        future.get();

        //then
        ConsumerRecords<?, ?> consumerRecords = KafkaTestUtils.getRecords(testConsumer, Duration.ofSeconds(10L), 2);
        Assertions.assertThat(KafkaUtils.noOfRecordsForTopic(consumerRecords, COMPANY_EXEMPTIONS_DELTA_TOPIC)).isOne();
        assertThat(KafkaUtils.noOfRecordsForTopic(consumerRecords, COMPANY_EXEMPTIONS_DELTA_INVALID_TOPIC)).isOne();
        assertThat(KafkaUtils.noOfRecordsForTopic(consumerRecords, COMPANY_EXEMPTIONS_DELTA_RETRY_TOPIC)).isZero();
        assertThat(KafkaUtils.noOfRecordsForTopic(consumerRecords, COMPANY_EXEMPTIONS_DELTA_ERROR_TOPIC)).isZero();
    }
}
