package uk.gov.companieshouse.exemptions.delta.upsert;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.exemptions.delta.NonRetryableException;
import uk.gov.companieshouse.exemptions.delta.RetryableException;

import java.util.function.Supplier;
import uk.gov.companieshouse.logging.Logger;

/**
 * Upserts a company exemptions resource via a REST HTTP request.
 */
@Component
class UpsertClient {

    private final Supplier<InternalApiClient> internalApiClientFactory;
    private final Logger logger;

    UpsertClient(Supplier<InternalApiClient> internalApiClientFactory, Logger logger) {
        this.internalApiClientFactory = internalApiClientFactory;
        this.logger = logger;
    }

    /**
     * Upsert a company exemptions resource via a REST HTTP request.
     *
     * @param request A {@link UpsertRequest request object} containing data that will be upserted and the path to which
     *                it will be sent.
     */
    void upsert(UpsertRequest request) {
        InternalApiClient client = internalApiClientFactory.get();
        try {
            client.privateDeltaCompanyAppointmentResourceHandler()
                    .upsertCompanyExemptionsResource(request.getPath(), request.getBody())
                    .execute();
        } catch (ApiErrorResponseException e) {
            if(e.getStatusCode() / 100 == 5) {
                logger.error(String.format("Server error returned with status code: [%s] when upserting delta", e.getStatusCode()));
                throw new RetryableException("Server error returned when upserting delta", e);
            } else {
                logger.error(String.format("Upsert client error returned with status code: [%s] when upserting delta", e.getStatusCode()));
                throw new NonRetryableException("UpsertClient error returned when upserting delta", e);
            }
        } catch (IllegalArgumentException e) {
            logger.error("Illegal argument exception caught when handling API response");
            throw new RetryableException("Server error returned when upserting delta", e);
        } catch (URIValidationException e) {
            logger.error("Invalid path specified when handling API request");
            throw new NonRetryableException("Invalid path specified", e);
        }
    }
}
