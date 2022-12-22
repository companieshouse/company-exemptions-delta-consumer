package uk.gov.companieshouse.exemptions.delta;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@CucumberContextConfiguration
@AutoConfigureMockMvc
@SpringBootTest(classes = Application.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@EmbeddedKafka(
        topics = {"company-exemptions-delta", "company-exemptions-delta-company-exemptions-delta-consumer-retry",
                "company-exemptions-delta-company-exemptions-delta-consumer-error"},
        controlledShutdown = true,
        partitions = 1
)
@TestPropertySource(locations = "classpath:application-integration_consumer_upsert.yml")
@Import(TestConfig.class)
@ActiveProfiles("integration_consumer_upsert")
public class Configuration {
}
