package uk.gov.companieshouse.exemptions.delta.upsert;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.delta.PscExemptionDelta;
import uk.gov.companieshouse.exemptions.delta.Service;
import uk.gov.companieshouse.exemptions.delta.ServiceParameters;

@Component
class Orchestrator implements Service {

    private final ContentFilter contentFilter;
    private final UpsertRequestMapper mapper;
    private final Client client;

    Orchestrator(ContentFilter contentFilter, UpsertRequestMapper mapper, Client client) {
        this.contentFilter = contentFilter;
        this.mapper = mapper;
        this.client = client;
    }

    @Override
    public void processMessage(ServiceParameters parameters) {
        PscExemptionDelta delta = contentFilter.filter(parameters.getDelta());
        Request request = mapper.mapDelta(delta);
        client.upsert(request);
    }
}
