package uk.gov.companieshouse.exemptions.delta.exemptions;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.reflect.ReflectDatumWriter;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import uk.gov.companieshouse.delta.ChsDelta;
import com.github.tomakehurst.wiremock.WireMockServer;
import uk.gov.companieshouse.exemptions.delta.util.TestDataHelper;

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

    @Given("the company exemptions delta consumer service is running")
    public void theApplicationRunning() {
        // TODO: Change code where necessary
        assertThat(embeddedKafkaBroker).isNotNull();
    }

    @When("^the topic receives a message containing a valid CHS delta payload")
    public void consumerReceivesExemptionDeltaRequest() throws IOException, InterruptedException {
        // TODO: Change code where necessary
        configureWiremock();
        stubPutExemptions(200);
        this.output = TestDataHelper.getOutputData();

        ChsDelta delta = new ChsDelta(TestDataHelper.getInputData(), 1, "123", false);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Encoder encoder = EncoderFactory.get().directBinaryEncoder(outputStream, null);
        DatumWriter<ChsDelta> writer = new ReflectDatumWriter<>(ChsDelta.class);
        writer.write(delta, encoder);
        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(testConsumer);

        testProducer.send(new ProducerRecord<>("company-exemptions-delta", 0, System.currentTimeMillis(), "key", outputStream.toByteArray()));
        assertThat(latch.await(5, TimeUnit.SECONDS)).isTrue();
    }

    @Then("a PUT request is sent to the company exemptions data api with the transformed data")
    public void putRequestSentToCompanyExemptionsDataApi() {
        // TODO Add checks for the output message
        verify(1, putRequestedFor(urlEqualTo("/company-exemptions/00006400/internal")));
    }

    private void stubPutExemptions(int responseCode) {
        stubFor(put(urlEqualTo("/company-exemptions/00006400/internal"))
                .willReturn(ok()));
    }

    private void configureWiremock() {
        if (wireMockServer == null) {
            wireMockServer = new WireMockServer(Integer.parseInt(port));
            wireMockServer.start();
            configureFor("localhost", Integer.parseInt(port));
        } else{
            resetWiremock();
        }
    }

    private void resetWiremock(){
        if(wireMockServer == null){
            throw new RuntimeException("Wiremock not initialised");
        }
        wireMockServer.resetRequests();
    }
}
