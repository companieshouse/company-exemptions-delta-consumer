package uk.gov.companieshouse.exemptions.delta.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.testcontainers.shaded.com.google.common.collect.Iterables;

public final class KafkaUtils {

    public static final String COMPANY_EXEMPTIONS_DELTA_TOPIC = "company-exemptions-delta";
    public static final String COMPANY_EXEMPTIONS_DELTA_INVALID_TOPIC = "company-exemptions-delta-company-exemptions-delta-consumer-invalid";
    public static final String COMPANY_EXEMPTIONS_DELTA_RETRY_TOPIC = "company-exemptions-delta-company-exemptions-delta-consumer-retry";
    public static final String COMPANY_EXEMPTIONS_DELTA_ERROR_TOPIC = "company-exemptions-delta-company-exemptions-delta-consumer-error";

    private KafkaUtils(){
    }

    public static int noOfRecordsForTopic(ConsumerRecords<?, ?> records, String topic) {
        return Iterables.size(records.records(topic));
    }
}
