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
                "company-exemptions-delta-company-exemptions-delta-consumer-error", "company-exemptions-delta-company-exemptions-delta-consumer-invalid"},
        controlledShutdown = true,
        partitions = 1
)
@Import(TestConfig.class)
@ActiveProfiles("integration_tests")
public class Configuration {
}
