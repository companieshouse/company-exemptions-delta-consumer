package uk.gov.companieshouse.exemptions.delta.delete;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.exemptions.delta.NonRetryableException;
import uk.gov.companieshouse.exemptions.delta.RetryableException;

import java.util.function.Supplier;

/**
 * Deletes a company exemptions resource via a REST HTTP request.
 */
@Component
class DeleteClient {

    private final Supplier<InternalApiClient> internalApiClientFactory;

    DeleteClient(Supplier<InternalApiClient> internalApiClientFactory) {
        this.internalApiClientFactory = internalApiClientFactory;
    }

    /**
     * Delete a company exemptions resource via a REST HTTP request.
     *
     * @param request A {@link DeleteRequest request object} containing data that will be upserted and the path to which
     *                it will be sent.
     */
    void delete(DeleteRequest request) {
        try {
            InternalApiClient client = internalApiClientFactory.get();
            client.privateDeltaCompanyAppointmentResourceHandler()
                    .deleteCompanyExemptionsResource(request.getPath())
                    .execute();
        } catch (ApiErrorResponseException e) {
            if (e.getStatusCode() / 100 == 5) {
                throw new RetryableException("Server error returned when deleting delta", e);
            } else {
                throw new NonRetryableException("DeleteClient error returned when deleting delta", e);
            }
        } catch (URIValidationException e) {
            throw new NonRetryableException("Invalid path specified", e);
        }
    }
}
