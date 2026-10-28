package uk.gov.companieshouse.exemptions.delta.kafka;

import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@Import(TestKafkaConfig.class)
public abstract class AbstractKafkaTest {

    @Container
    public static final ConfluentKafkaContainer kafkaContainer = new ConfluentKafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.8.0"));

    // Prevents a TOPIC environment variable on the host from overriding the consumer topic
    @DynamicPropertySource
    static void pinConsumerTopic(DynamicPropertyRegistry registry) {
        registry.add("consumer.topic", () -> KafkaUtils.COMPANY_EXEMPTIONS_DELTA_TOPIC);
    }
}
