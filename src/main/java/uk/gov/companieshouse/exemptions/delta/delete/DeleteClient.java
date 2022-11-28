package uk.gov.companieshouse.exemptions.delta.delete;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.exemptions.delta.NonRetryableException;
import uk.gov.companieshouse.exemptions.delta.RetryableException;

import java.util.function.Supplier;
import uk.gov.companieshouse.logging.Logger;

/**
 * Deletes a company exemptions resource via a REST HTTP request.
 */
@Component
class DeleteClient {

    private final Supplier<InternalApiClient> internalApiClientFactory;
    private final Logger logger;

    DeleteClient(Supplier<InternalApiClient> internalApiClientFactory, Logger logger) {
        this.internalApiClientFactory = internalApiClientFactory;
        this.logger = logger;
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
                    .deleteCompanyExemptionsResource(request.getPath())
                    .execute();
        } catch (ApiErrorResponseException e) {
            if (e.getStatusCode() / 100 == 5) {
                logger.error(String.format("Server error returned with status code: [%s] when deleting delta", e.getStatusCode()));
                throw new RetryableException("Server error returned when deleting delta", e);
            } else {
                logger.error(String.format("Delete client error returned with status code: [%s] when deleting delta", e.getStatusCode()));
                throw new NonRetryableException("DeleteClient error returned when deleting delta", e);
            }
        } catch (IllegalArgumentException e) {
            logger.error("Illegal argument exception caught when handling API response");
            throw new RetryableException("Server error returned when deleting delta", e);
        } catch (URIValidationException e) {
            logger.error("Invalid path specified when handling API request");
            throw new NonRetryableException("Invalid path specified", e);
        }
    }
}
