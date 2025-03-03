package uk.gov.companieshouse.exemptions.delta.config;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;
import uk.gov.companieshouse.exemptions.delta.kafka.TestKafkaConfig;

@CucumberContextConfiguration
@AutoConfigureMockMvc
@SpringBootTest
@Import(TestKafkaConfig.class)
@ActiveProfiles("integration_tests")
public class Configuration {

    public static final ConfluentKafkaContainer ConfluentKafkaContainer = new ConfluentKafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:latest"));


    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", ConfluentKafkaContainer::getBootstrapServers);
        ConfluentKafkaContainer.start();
    }

}
