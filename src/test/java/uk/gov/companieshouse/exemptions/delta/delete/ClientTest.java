package uk.gov.companieshouse.exemptions.delta.delete;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.delta.PrivateDeltaResourceHandler;
import uk.gov.companieshouse.api.handler.delta.exemptions.request.PrivateCompanyExemptionsDelete;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.exemptions.delta.ResponseHandler;

@ExtendWith(MockitoExtension.class)
public class ClientTest {

    private static final String REQUEST_PATH = "/company-exemptions/12345678/internal";
    private static final String DELTA_AT = "20190612181928152002";
    private static final String INVALID_PATH = "invalid";

    @Mock
    private InternalApiClient internalApiClient;

    @Mock
    private ResponseHandler handler;

    @Mock
    private PrivateDeltaResourceHandler resourceHandler;

    @Mock
    private PrivateCompanyExemptionsDelete companyExemptionsDelete;

    @Test
    void testDelete() throws ApiErrorResponseException, URIValidationException {
        // given
        when(internalApiClient.privateDeltaCompanyAppointmentResourceHandler()).thenReturn(resourceHandler);
        when(resourceHandler.deleteCompanyExemptionsResource(anyString(), anyString())).thenReturn(
                companyExemptionsDelete);
        DeleteRequest request = new DeleteRequest(REQUEST_PATH, DELTA_AT);
        DeleteClient client = new DeleteClient(() -> internalApiClient, handler);

        // when
        client.delete(request);

        // then
        verify(internalApiClient).privateDeltaCompanyAppointmentResourceHandler();
        verify(resourceHandler).deleteCompanyExemptionsResource(REQUEST_PATH, DELTA_AT);
        verify(companyExemptionsDelete).execute();
    }

    @Test
    void testDeleteHandles400BadRequestErrorResponse() throws ApiErrorResponseException, URIValidationException {
        ApiErrorResponseException exception = new ApiErrorResponseException(
                new HttpResponseException.Builder(400, "Bad request", new HttpHeaders()));

        // given
        when(internalApiClient.privateDeltaCompanyAppointmentResourceHandler()).thenReturn(resourceHandler);
        when(resourceHandler.deleteCompanyExemptionsResource(anyString(), anyString())).thenReturn(
                companyExemptionsDelete);
        when(companyExemptionsDelete.execute()).thenThrow(exception);
        DeleteRequest request = new DeleteRequest(REQUEST_PATH, DELTA_AT);
        DeleteClient client = new DeleteClient(() -> internalApiClient, handler);

        // when
        client.delete(request);

        // then
        verify(internalApiClient).privateDeltaCompanyAppointmentResourceHandler();
        verify(resourceHandler).deleteCompanyExemptionsResource(REQUEST_PATH, DELTA_AT);
        verify(companyExemptionsDelete).execute();
        verify(handler).handle(exception);
    }

    @Test
    void testDeleteHandles503ServiceUnavailableErrorResponse() throws ApiErrorResponseException, URIValidationException {
        ApiErrorResponseException exception = new ApiErrorResponseException(new HttpResponseException.Builder(
                503, "Service Unavailable", new HttpHeaders()));
        // given
        when(internalApiClient.privateDeltaCompanyAppointmentResourceHandler()).thenReturn(resourceHandler);
        when(resourceHandler.deleteCompanyExemptionsResource(anyString(), anyString())).thenReturn(
                companyExemptionsDelete);
        when(companyExemptionsDelete.execute()).thenThrow(exception);
        DeleteRequest request = new DeleteRequest(REQUEST_PATH, DELTA_AT);
        DeleteClient client = new DeleteClient(() -> internalApiClient, handler);

        // when
        client.delete(request);

        // then
        verify(internalApiClient).privateDeltaCompanyAppointmentResourceHandler();
        verify(resourceHandler).deleteCompanyExemptionsResource(REQUEST_PATH, DELTA_AT);
        verify(companyExemptionsDelete).execute();
        verify(handler).handle(exception);
    }

    @Test
    void testDeleteHandlesURIException() throws ApiErrorResponseException, URIValidationException {
        Class<URIValidationException> exceptionClass = URIValidationException.class;

        // given
        when(internalApiClient.privateDeltaCompanyAppointmentResourceHandler()).thenReturn(resourceHandler);
        when(resourceHandler.deleteCompanyExemptionsResource(anyString(), anyString())).thenReturn(
                companyExemptionsDelete);
        when(companyExemptionsDelete.execute()).thenThrow(exceptionClass);
        DeleteRequest request = new DeleteRequest(INVALID_PATH, DELTA_AT);
        DeleteClient client = new DeleteClient(() -> internalApiClient, handler);

        // when
        client.delete(request);

        // then
        verify(internalApiClient).privateDeltaCompanyAppointmentResourceHandler();
        verify(resourceHandler).deleteCompanyExemptionsResource(INVALID_PATH, DELTA_AT);
        verify(companyExemptionsDelete).execute();
        verify(handler).handle(any(exceptionClass));
    }
}
