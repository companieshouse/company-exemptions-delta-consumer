package uk.gov.companieshouse.exemptions.delta.kafka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static uk.gov.companieshouse.exemptions.delta.kafka.KafkaUtils.COMPANY_EXEMPTIONS_DELTA_ERROR_TOPIC;
import static uk.gov.companieshouse.exemptions.delta.kafka.KafkaUtils.COMPANY_EXEMPTIONS_DELTA_INVALID_TOPIC;
import static uk.gov.companieshouse.exemptions.delta.kafka.KafkaUtils.COMPANY_EXEMPTIONS_DELTA_RETRY_TOPIC;
import static uk.gov.companieshouse.exemptions.delta.kafka.KafkaUtils.COMPANY_EXEMPTIONS_DELTA_TOPIC;
import static uk.gov.companieshouse.exemptions.delta.kafka.KafkaUtils.noOfRecordsForTopic;

import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.reflect.ReflectDatumWriter;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.companieshouse.delta.ChsDelta;
import uk.gov.companieshouse.exemptions.delta.service.ServiceRouter;

@SpringBootTest
class ConsumerPositiveTest extends AbstractKafkaTest {

    @Autowired
    private KafkaConsumer<String, byte[]> testConsumer;
    @Autowired
    private KafkaProducer<String, byte[]> testProducer;
    @Autowired
    private ConsumerAspect consumerAspect;

    @MockitoBean
    private ServiceRouter router;

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
        registry.add("steps", () -> 1);
    }

    @BeforeEach
    public void setup() {
        consumerAspect.resetLatch();
        testConsumer.poll(Duration.ofMillis(1000));
    }

    @Test
    void testConsumeMessage() throws Exception {
        //given
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Encoder encoder = EncoderFactory.get().directBinaryEncoder(outputStream, null);
        DatumWriter<ChsDelta> writer = new ReflectDatumWriter<>(ChsDelta.class);
        writer.write(new ChsDelta("{}", 0, "context_id", false), encoder);

        //when
        testProducer.send(new ProducerRecord<>(COMPANY_EXEMPTIONS_DELTA_TOPIC, 0, System.currentTimeMillis(), "key", outputStream.toByteArray()));
        if (!consumerAspect.getLatch().await(30L, TimeUnit.SECONDS)) {
            fail("Timed out waiting for latch");
        }
        ConsumerRecords<?, ?> records = KafkaTestUtils.getRecords(testConsumer, Duration.ofSeconds(10L), 1);
        assertThat(noOfRecordsForTopic(records, COMPANY_EXEMPTIONS_DELTA_TOPIC)).isOne();
        assertThat(noOfRecordsForTopic(records, COMPANY_EXEMPTIONS_DELTA_INVALID_TOPIC)).isZero();
        assertThat(noOfRecordsForTopic(records, COMPANY_EXEMPTIONS_DELTA_RETRY_TOPIC)).isZero();
        assertThat(noOfRecordsForTopic(records, COMPANY_EXEMPTIONS_DELTA_ERROR_TOPIC)).isZero();

        //then
        verify(router).route(any());
    }
}
