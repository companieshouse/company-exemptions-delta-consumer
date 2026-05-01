package uk.gov.companieshouse.exemptions.delta.kafka;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@Import(TestKafkaConfig.class)
public abstract class AbstractKafkaTest {

    @BeforeAll
    static void beforeAll() {
        System.setProperty("api.version", "1.44");
    }

    @Container
    public static final KafkaContainer kafkaContainer = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:5.0.0"));}
