package uk.gov.companieshouse.exemptions.delta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static final String NAMESPACE = "company-exemptions-delta-consumer";

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
