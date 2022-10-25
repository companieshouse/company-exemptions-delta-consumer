package uk.gov.companieshouse.exemptions.delta;

import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.delta.ChsDelta;

@Component
public class ServiceRouter {
    public void route(Message<ChsDelta> delta) {

    }
}
