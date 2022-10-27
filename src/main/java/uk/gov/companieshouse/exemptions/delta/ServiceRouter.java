package uk.gov.companieshouse.exemptions.delta;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.delta.ChsDelta;

/**
 * Routes a {@link ChsDelta delta} to a service.
 */
@Component
public class ServiceRouter {

    private final Service upsertService;
    private final Service deleteService;

    public ServiceRouter(Service upsertService, Service deleteService) {
        this.upsertService = upsertService;
        this.deleteService = deleteService;
    }

    /**
     * Routes a {@link ChsDelta delta} to a service based on its internal state.<br>
     * <br>
     * If {@link ChsDelta#getIsDelete() isDelete} is true then the delta will be routed to the {@link Service delete service}.
     * If {@link ChsDelta#getIsDelete() isDelete} is false then the delta will be routed to the {@link Service upsert service}.
     *
     * @param delta A {@link ChsDelta delta} containing an
     * {@link uk.gov.companieshouse.api.delta.PscExemptionDelta upsert exemption delta} or
     * a {@link uk.gov.companieshouse.api.delta.PscExemptionDeleteDelta delete exemption delta}.
     */
    public void route(ChsDelta delta) {
        ServiceParameters parameters = new ServiceParameters(delta);
        if (delta.getIsDelete()) {
            deleteService.processMessage(parameters);
        } else {
            upsertService.processMessage(parameters);
        }
    }
}
