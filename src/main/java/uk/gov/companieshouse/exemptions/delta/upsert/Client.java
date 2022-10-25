package uk.gov.companieshouse.exemptions.delta.upsert;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.exemptions.delta.NonRetryableException;
import uk.gov.companieshouse.exemptions.delta.RetryableException;

import java.util.function.Supplier;

@Component
class Client {

    private final Supplier<InternalApiClient> internalApiClientFactory;

    Client(Supplier<InternalApiClient> internalApiClientFactory) {
        this.internalApiClientFactory = internalApiClientFactory;
    }

    void upsert(Request request) {
        InternalApiClient client = internalApiClientFactory.get();
        try {
            client.privateDeltaCompanyAppointmentResourceHandler()
                    .upsertCompanyExemptionsResource(request.getPath(), request.getBody())
                    .execute();
        } catch (ApiErrorResponseException e) {
            if(e.getStatusCode() / 100 == 5) {
                throw new RetryableException("Server error returned when upserting delta", e);
            } else {
                throw new NonRetryableException("Client error returned when upserting delta", e);
            }
        } catch (URIValidationException e) {
            throw new NonRetryableException("Invalid path specified", e);
        }
    }
}
