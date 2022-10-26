package uk.gov.companieshouse.exemptions.delta.delete;

import uk.gov.companieshouse.api.delta.PscExemptionDeleteDelta;
import uk.gov.companieshouse.exemptions.delta.Service;
import uk.gov.companieshouse.exemptions.delta.ServiceParameters;

public class Orchestrator implements Service {

    private final ContentFilter filter;
    private final DeleteRequestMapper mapper;
    private final Client client;

    public Orchestrator(ContentFilter filter, DeleteRequestMapper mapper, Client client) {
        this.filter = filter;
        this.mapper = mapper;
        this.client = client;
    }

    @Override
    public void processMessage(ServiceParameters parameters) {
        PscExemptionDeleteDelta delta = filter.filter(parameters.getDelta());
        Request request = mapper.mapDelta(delta);
        client.delete(request);
    }
}
