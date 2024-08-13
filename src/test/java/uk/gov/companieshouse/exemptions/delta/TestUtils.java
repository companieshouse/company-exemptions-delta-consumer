package uk.gov.companieshouse.exemptions.delta;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

public final class TestUtils {

    static final String COMPANY_EXEMPTIONS_DELTA_TOPIC = "company-exemptions-delta";
    static final String COMPANY_EXEMPTIONS_DELTA_INVALID_TOPIC = "company-exemptions-delta-company-exemptions-delta-consumer-invalid";
    static final String COMPANY_EXEMPTIONS_DELTA_RETRY_TOPIC = "company-exemptions-delta-company-exemptions-delta-consumer-retry";
    static final String COMPANY_EXEMPTIONS_DELTA_ERROR_TOPIC = "company-exemptions-delta-company-exemptions-delta-consumer-error";

    private TestUtils(){
    }

    public static int noOfRecordsForTopic(ConsumerRecords<?, ?> records, String topic) {
        int count = 0;
        for (ConsumerRecord<?, ?> ignored : records.records(topic)) {
            count++;
        }
        return count;
    }
}
