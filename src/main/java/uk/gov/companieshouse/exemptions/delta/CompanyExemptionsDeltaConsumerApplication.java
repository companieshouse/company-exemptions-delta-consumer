package uk.gov.companieshouse.exemptions.delta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CompanyExemptionsDeltaConsumerApplication {

    public static final String NAMESPACE = "company-exemptions-delta-consumer";

    public static void main(String[] args) {
        SpringApplication.run(CompanyExemptionsDeltaConsumerApplication.class, args);
    }
}
