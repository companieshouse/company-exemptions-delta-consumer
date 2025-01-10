package uk.gov.companieshouse.exemptions.delta.upsert;

import java.util.function.Supplier;

import org.springframework.stereotype.Component;

import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.exemptions.delta.ResponseHandler;

;

/**
 * Upserts a company exemptions resource via a REST HTTP request.
 */
@Component
class UpsertClient {

    private final Supplier<InternalApiClient> internalApiClientFactory;

    private final ResponseHandler handler;

    UpsertClient(Supplier<InternalApiClient> internalApiClientFactory, ResponseHandler handler) {
        this.internalApiClientFactory = internalApiClientFactory;
        this.handler = handler;
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
            }  catch (ApiErrorResponseException e) {
            handler.handle(e);
        } catch (URIValidationException e) {
            handler.handle(e);
        }
    }
}
