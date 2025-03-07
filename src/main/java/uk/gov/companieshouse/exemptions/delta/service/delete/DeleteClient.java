package uk.gov.companieshouse.exemptions.delta.service.delete;

import java.util.function.Supplier;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.exemptions.delta.service.ResponseHandler;

/**
 * Deletes a company exemptions resource via a REST HTTP request.
 */
@Component
class DeleteClient {

    private final Supplier<InternalApiClient> internalApiClientFactory;

    private final ResponseHandler handler;

    DeleteClient(Supplier<InternalApiClient> internalApiClientFactory, ResponseHandler handler) {
        this.internalApiClientFactory = internalApiClientFactory;
        this.handler = handler;
    }

    /**
     * Delete a company exemptions resource via a REST HTTP request.
     *
     * @param request A {@link DeleteRequest request object} containing the path to which the delete
     *                request will be sent.
     */
    void delete(DeleteRequest request) {
        InternalApiClient client = internalApiClientFactory.get();
        try {
            client.privateDeltaCompanyAppointmentResourceHandler()
                    .deleteCompanyExemptionsResource(request.getPath(), request.getDeltaAt())
                    .execute();
        } catch (ApiErrorResponseException e) {
            handler.handle(e);
        } catch (URIValidationException e) {
            handler.handle(e);
        }
    }
}
