package uk.gov.companieshouse.exemptions.delta.delete;

import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.exemptions.delta.NonRetryableException;
import uk.gov.companieshouse.exemptions.delta.RetryableException;

import java.util.function.Supplier;

public class Client {

    private final Supplier<InternalApiClient> internalApiClientFactory;

    public Client(Supplier<InternalApiClient> internalApiClientFactory) {
        this.internalApiClientFactory = internalApiClientFactory;
    }

    public void delete(Request request) {
        try {
            InternalApiClient client = internalApiClientFactory.get();
            client.privateDeltaCompanyAppointmentResourceHandler()
                    .deleteCompanyExemptionsResource(request.getPath())
                    .execute();
        } catch (ApiErrorResponseException e) {
            if (e.getStatusCode() / 100 == 5) {
                throw new RetryableException("Server error returned when deleting delta", e);
            } else {
                throw new NonRetryableException("Client error returned when deleting delta", e);
            }
        } catch (URIValidationException e) {
            throw new NonRetryableException("Invalid path specified", e);
        }
    }
}
