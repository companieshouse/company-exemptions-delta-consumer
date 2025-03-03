package uk.gov.companieshouse.exemptions.delta.steps;


import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.getAllServeEvents;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.companieshouse.exemptions.delta.config.Configuration.ConfluentKafkaContainer;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.reflect.ReflectDatumWriter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import uk.gov.companieshouse.delta.ChsDelta;
import uk.gov.companieshouse.exemptions.delta.consumer.ConsumerAspect;
import uk.gov.companieshouse.exemptions.delta.data.TestData;

public class ExemptionsConsumerSteps {

    private static final int MESSAGE_CONSUMED_TIMEOUT = 5;
    private static WireMockServer wireMockServer;

    @Value("${wiremock.server.port}")
    private String port;

    @Autowired
    private KafkaConsumer<String, byte[]> testConsumer;
    @Autowired
    private KafkaProducer<String, byte[]> testProducer;
    @Autowired
    private ConsumerAspect consumerAspect;

    @Before
    public void cleanUp() {
        if (!ConfluentKafkaContainer.isRunning()) {
            ConfluentKafkaContainer.start();
        }
        consumerAspect.resetLatch();
        testConsumer.poll(Duration.ofMillis(1000));
    }

    @When("^the topic receives a message containing a valid CHS upsert delta payload")
    public void consumerReceivesExemptionDeltaRequest() throws IOException, InterruptedException {
        configureWiremock();
        stubPutExemptions(HttpStatus.OK.value());

        ChsDelta delta = new ChsDelta(TestData.getInputData(), 1, "123", false);
        sendDeltaToMainTopic(delta);

        assertMessageConsumed();
    }

    @When("the consumer receives a message with invalid payload")
    public void messageWithInvalidDataIsSent() throws Exception {
        ChsDelta delta = new ChsDelta("invalidData", 1, "123", false);
        sendDeltaToMainTopic(delta);

        assertMessageConsumed();
    }

    @When("an invalid avro message is sent")
    public void invalidAvroMessageIsSent() throws Exception {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Encoder encoder = EncoderFactory.get().directBinaryEncoder(outputStream, null);
        DatumWriter<String> writer = new ReflectDatumWriter<>(String.class);
        writer.write("invalidData", encoder);

        testProducer.send(new ProducerRecord<>("company-exemptions-delta", 0, System.currentTimeMillis(), "key", outputStream.toByteArray()));
    }

    @When("the topic receives a message with a valid CHS delete delta payload")
    public void consumerReceivesExemptionsDeltaDeleteRequest() throws IOException, InterruptedException {
        configureWiremock();
        stubDeleteExemptions(HttpStatus.OK.value());

        ChsDelta delta = new ChsDelta(TestData.getDeleteData(), 1, "123", true);
        sendDeltaToMainTopic(delta);

        assertMessageConsumed();
    }

    @Then("^the message should be moved to topic (.*)$")
    public void theMessageShouldBeMovedToTopic(String topic) {
        ConsumerRecord<String, byte[]> singleRecord = KafkaTestUtils.getSingleRecord(testConsumer, topic);

        assertThat(singleRecord.value()).isNotNull();
    }

    @When("^the consumer receives a message but the data api returns a (\\d{1,3}) status code")
    public void consumerReceivesMessageButDataApiReturns(int responseCode) throws Exception {
        configureWiremock();
        stubPutExemptions(responseCode);

        ChsDelta delta = new ChsDelta(TestData.getInputData(), 1, "123", false);
        sendDeltaToMainTopic(delta);

        assertMessageConsumed();

    }

    @Then("the message should retry (\\d*) times and then error$")
    public void theMessageShouldRetryAndError(int retries) {
        ConsumerRecords<String, byte[]> records = KafkaTestUtils.getRecords(testConsumer, Duration.ofSeconds(10L), 6);
        Iterable<ConsumerRecord<String, byte[]>> retryRecords = records.records("company-exemptions-delta-company-exemptions-delta-consumer-retry");
        Iterable<ConsumerRecord<String, byte[]>> errorRecords = records.records("company-exemptions-delta-company-exemptions-delta-consumer-error");

        int actualRetries = (int) StreamSupport.stream(retryRecords.spliterator(), false).count();
        int errors = (int) StreamSupport.stream(errorRecords.spliterator(), false).count();

        assertThat(actualRetries).isEqualTo(retries);
        assertThat(errors).isEqualTo(1);

    }

    @Then("a DELETE request is sent to the company exemptions data api")
    public void deleteRequestSentToCompanyExemptionsDataApi() {
        verify(exactly(1), deleteRequestedFor(urlEqualTo("/company-exemptions/00006400/internal")));

        List<ServeEvent> wiremockEvents = getAllServeEvents();
        assertEquals(1, wiremockEvents.size());
        assertEquals(HttpStatus.OK.value(), wiremockEvents.get(0).getResponse().getStatus());
    }

    @Then("a PUT request is sent to the company exemptions data api with the transformed data")
    public void putRequestSentToCompanyExemptionsDataApi() {
        verify(exactly(1), putRequestedFor(urlEqualTo("/company-exemptions/00006400/internal")));

        List<ServeEvent> wiremockEvents = getAllServeEvents();
        assertEquals(1, wiremockEvents.size());

        String requestBody = new String(wiremockEvents.get(0).getRequest().getBody());
        assertThat(requestBody).isNotNull();
        assertEquals(200, wiremockEvents.get(0).getResponse().getStatus());
    }

    private void stubPutExemptions(int responseCode) {
        stubFor(put(urlEqualTo("/company-exemptions/00006400/internal"))
                .willReturn(aResponse().withStatus(responseCode)));
    }

    private void stubDeleteExemptions(int responseCode) {
        stubFor(delete(urlEqualTo("/company-exemptions/00006400/internal"))
                .willReturn(aResponse().withStatus(responseCode)));
    }

    private void configureWiremock() {
        if (wireMockServer == null) {
            wireMockServer = new WireMockServer(Integer.parseInt(port));
            wireMockServer.start();
            configureFor("localhost", Integer.parseInt(port));
        } else {
            resetWiremock();
        }
    }

    private void resetWiremock() {
        if (wireMockServer == null) {
            throw new RuntimeException("Wiremock not initialised");
        }
        wireMockServer.resetRequests();
    }

    private void sendDeltaToMainTopic(ChsDelta delta) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Encoder encoder = EncoderFactory.get().directBinaryEncoder(outputStream, null);
        DatumWriter<ChsDelta> writer = new ReflectDatumWriter<>(ChsDelta.class);
        writer.write(delta, encoder);

        testProducer.send(new ProducerRecord<>("company-exemptions-delta", 0, System.currentTimeMillis(), "key", outputStream.toByteArray()));
    }

    private void assertMessageConsumed() throws InterruptedException {
        assertThat(consumerAspect.getLatch().await(MESSAGE_CONSUMED_TIMEOUT, TimeUnit.SECONDS)).isTrue();
    }
}
