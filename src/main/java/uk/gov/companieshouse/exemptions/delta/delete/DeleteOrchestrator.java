package uk.gov.companieshouse.exemptions.delta.delete;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.delta.PscExemptionDeleteDelta;
import uk.gov.companieshouse.exemptions.delta.Service;
import uk.gov.companieshouse.exemptions.delta.ServiceParameters;

/**
 * Orchestrates all steps required to delete a company exemptions resource.
 */
@Component
class DeleteOrchestrator implements Service {

    private final DeleteContentFilter filter;
    private final DeleteRequestMapper mapper;
    private final DeleteClient client;

    DeleteOrchestrator(DeleteContentFilter filter, DeleteRequestMapper mapper, DeleteClient client) {
        this.filter = filter;
        this.mapper = mapper;
        this.client = client;
    }

    @Override
    public void processMessage(ServiceParameters parameters) {
        PscExemptionDeleteDelta delta = filter.filter(parameters.getDelta());
        DeleteRequest request = mapper.mapDelta(delta);
        client.delete(request);
    }
}
