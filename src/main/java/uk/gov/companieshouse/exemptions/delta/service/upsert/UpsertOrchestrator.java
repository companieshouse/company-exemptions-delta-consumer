package uk.gov.companieshouse.exemptions.delta.service.upsert;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.delta.PscExemptionDelta;
import uk.gov.companieshouse.exemptions.delta.service.Service;
import uk.gov.companieshouse.exemptions.delta.service.ServiceParameters;

/**
 * Orchestrates all steps required to upsert a company exemptions resource.
 */
@Component("upsertService")
class UpsertOrchestrator implements Service {

    private final UpsertContentFilter contentFilter;
    private final UpsertRequestMapper mapper;
    private final UpsertClient client;

    UpsertOrchestrator(UpsertContentFilter contentFilter, UpsertRequestMapper mapper, UpsertClient client) {
        this.contentFilter = contentFilter;
        this.mapper = mapper;
        this.client = client;
    }

    @Override
    public void processMessage(ServiceParameters parameters) {
        PscExemptionDelta delta = contentFilter.filter(parameters.getDelta());
        UpsertRequest request = mapper.mapDelta(delta);
        client.upsert(request);
    }
}
