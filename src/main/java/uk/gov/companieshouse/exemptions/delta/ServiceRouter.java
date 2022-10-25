package uk.gov.companieshouse.exemptions.delta;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.delta.ChsDelta;

@Component
public class ServiceRouter {

    private final Service upsertService;
    private final Service deleteService;

    public ServiceRouter(Service upsertService, Service deleteService) {
        this.upsertService = upsertService;
        this.deleteService = deleteService;
    }

    public void route(ChsDelta delta) {
        ServiceParameters parameters = new ServiceParameters(delta);
        if (delta.getIsDelete()) {
            deleteService.processMessage(parameters);
        } else {
            upsertService.processMessage(parameters);
        }
    }
}
