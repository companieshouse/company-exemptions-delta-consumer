package uk.gov.companieshouse.exemptions.delta;

import org.springframework.context.annotation.Import;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@Import(TestConfig.class)
public abstract class AbstractKafkaTest {

    @Container
    public static final ConfluentKafkaContainer confluentKafkaContainer = new ConfluentKafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:latest"));}
