package uk.gov.companieshouse.exemptions.delta.delete;

import static org.mockito.ArgumentMatchers.any;
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

    @Mock
    private InternalApiClient internalApiClient;

    @Mock
    private ResponseHandler handler;

    @Mock
    private PrivateDeltaResourceHandler resourceHandler;

    @Mock
    private PrivateCompanyExemptionsDelete deleteHandler;

    @Test
    void testDelete() throws ApiErrorResponseException, URIValidationException {
        // given
        when(internalApiClient.privateDeltaCompanyAppointmentResourceHandler()).thenReturn(resourceHandler);
        when(resourceHandler.deleteCompanyExemptionsResource(any())).thenReturn(deleteHandler);
        DeleteRequest request = new DeleteRequest(REQUEST_PATH);
        DeleteClient client = new DeleteClient(() -> internalApiClient, handler);

        // when
        client.delete(request);

        // then
        verify(internalApiClient).privateDeltaCompanyAppointmentResourceHandler();
        verify(resourceHandler).deleteCompanyExemptionsResource(any());
        verify(deleteHandler).execute();
        verify(resourceHandler).deleteCompanyExemptionsResource(REQUEST_PATH);
    }

    @Test
    void testDeleteHandles400BadRequestErrorResponse() throws ApiErrorResponseException, URIValidationException {
        ApiErrorResponseException exception = new ApiErrorResponseException(
                new HttpResponseException.Builder(400, "Bad request", new HttpHeaders()));

        // given
        when(internalApiClient.privateDeltaCompanyAppointmentResourceHandler()).thenReturn(resourceHandler);
        when(resourceHandler.deleteCompanyExemptionsResource(any())).thenReturn(deleteHandler);
        when(deleteHandler.execute()).thenThrow(exception);
        DeleteRequest request = new DeleteRequest(REQUEST_PATH);
        DeleteClient client = new DeleteClient(() -> internalApiClient, handler);

        // when
        client.delete(request);

        // then
        verify(internalApiClient).privateDeltaCompanyAppointmentResourceHandler();
        verify(resourceHandler).deleteCompanyExemptionsResource(any());
        verify(deleteHandler).execute();
        verify(handler).handle(exception);
    }

    @Test
    void testDeleteHandles503ServiceUnavailableErrorResponse() throws ApiErrorResponseException, URIValidationException {
        ApiErrorResponseException exception = new ApiErrorResponseException(new HttpResponseException.Builder(
                503, "Service Unavailable", new HttpHeaders()));
        // given
        when(internalApiClient.privateDeltaCompanyAppointmentResourceHandler()).thenReturn(resourceHandler);
        when(resourceHandler.deleteCompanyExemptionsResource(any())).thenReturn(deleteHandler);
        when(deleteHandler.execute()).thenThrow(exception);
        DeleteRequest request = new DeleteRequest(REQUEST_PATH);
        DeleteClient client = new DeleteClient(() -> internalApiClient, handler);

        // when
        client.delete(request);

        // then
        verify(internalApiClient).privateDeltaCompanyAppointmentResourceHandler();
        verify(resourceHandler).deleteCompanyExemptionsResource(any());
        verify(deleteHandler).execute();
        verify(handler).handle(exception);
    }

    @Test
    void testDeleteHandlesURIException() throws ApiErrorResponseException, URIValidationException {
        Class<URIValidationException> exceptionClass = URIValidationException.class;

        // given
        when(internalApiClient.privateDeltaCompanyAppointmentResourceHandler()).thenReturn(resourceHandler);
        when(resourceHandler.deleteCompanyExemptionsResource(any())).thenReturn(deleteHandler);
        when(deleteHandler.execute()).thenThrow(exceptionClass);
        DeleteRequest request = new DeleteRequest("invalid");
        DeleteClient client = new DeleteClient(() -> internalApiClient, handler);

        // when
        client.delete(request);

        // then
        verify(internalApiClient).privateDeltaCompanyAppointmentResourceHandler();
        verify(resourceHandler).deleteCompanyExemptionsResource(any());
        verify(deleteHandler).execute();
        verify(handler).handle(any(exceptionClass));
    }
}
