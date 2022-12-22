package uk.gov.companieshouse.exemptions.delta.exemptions;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.requestMadeFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import uk.gov.companieshouse.delta.ChsDelta;
import com.github.tomakehurst.wiremock.WireMockServer;
import uk.gov.companieshouse.exemptions.delta.util.ExemptionsRequestMatcher;
import uk.gov.companieshouse.exemptions.delta.util.TestDataHelper;
import uk.gov.companieshouse.logging.Logger;

public class ExemptionsConsumerSteps {


    @Value("${wiremock.server.port}")
    private String port;

    private String output;

    private static WireMockServer wireMockServer;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private KafkaConsumer<String, byte[]> testConsumer;

    @Autowired
    private KafkaProducer<String, byte[]> testProducer;

    @Autowired
    private CountDownLatch latch;

    @Autowired
    private Logger logger;

    @Given("the company exemptions delta consumer service is running")
    public void theApplicationRunning() {
        // TODO: Change code where necessary
        /*assertThat(embeddedKafkaBroker).isNotNull();*/
    }

    @When("^the topic receives a message containing a valid CHS delta payload")
    public void theConsumerReceivesDisqualificationOfType(String officerType, String disqType) throws Exception {
        // TODO: Change code where necessary
        /*configureWiremock();
        stubPutExemptions(200);
        this.output = TestDataHelper.getOutputData();

        ChsDelta delta = new ChsDelta(TestDataHelper.getInputData(), 1, "1", false);
        embeddedKafkaBroker.s .send(mainTopic, delta);

        countDown();*/
    }

    @Then("a PUT request is sent to the company exemptions data api with the transformed data")
    public void putRequestIsSentToTheDisqualificationsApi() {
        // TODO: Change code where necessary
        //verify(1, requestMadeFor(new ExemptionsRequestMatcher(logger, output)));
    }

    private void stubPutExemptions(int responseCode) {
        stubFor(put(urlEqualTo("/company-exemptions/00006400/internal"))
                .willReturn(aResponse().withStatus(responseCode)));
    }

    private void configureWiremock() {
        wireMockServer = new WireMockServer(Integer.parseInt(port));
        wireMockServer.start();
        configureFor("localhost", Integer.parseInt(port));
    }

    private void countDown() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        countDownLatch.await(5, TimeUnit.SECONDS);
    }
}
