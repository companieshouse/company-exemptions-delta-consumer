package uk.gov.companieshouse.exemptions.delta.delete;

import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.delta.PrivateDeltaResourceHandler;
import uk.gov.companieshouse.api.handler.delta.exemptions.request.PrivateCompanyExemptionsDelete;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.exemptions.delta.NonRetryableException;
import uk.gov.companieshouse.exemptions.delta.RetryableException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClientTest {

    private static final String REQUEST_PATH = "/company-exemptions/12345678/internal";

    @Mock
    private InternalApiClient internalApiClient;

    @Mock
    private PrivateDeltaResourceHandler resourceHandler;

    @Mock
    private PrivateCompanyExemptionsDelete deleteHandler;

    @Test
    void testDelete() throws ApiErrorResponseException, URIValidationException {
        // given
        when(internalApiClient.privateDeltaCompanyAppointmentResourceHandler()).thenReturn(resourceHandler);
        when(resourceHandler.deleteCompanyExemptionsResource(any())).thenReturn(deleteHandler);
        Request request = new Request(REQUEST_PATH);
        Client client = new Client(() -> internalApiClient);

        // when
        client.delete(request);

        // then
        verify(deleteHandler).execute();
        verify(resourceHandler).deleteCompanyExemptionsResource(REQUEST_PATH);
    }

    @Test
    void testThrowNonRetryableExceptionIfClientErrorReturned() throws ApiErrorResponseException, URIValidationException {
        // given
        when(internalApiClient.privateDeltaCompanyAppointmentResourceHandler()).thenReturn(resourceHandler);
        when(resourceHandler.deleteCompanyExemptionsResource(any())).thenReturn(deleteHandler);
        when(deleteHandler.execute()).thenThrow(new ApiErrorResponseException(new HttpResponseException.Builder(400, "Bad request", new HttpHeaders())));
        Request request = new Request(REQUEST_PATH);
        Client client = new Client(() -> internalApiClient);

        // when
        Executable actual = () -> client.delete(request);

        // then
        NonRetryableException exception = assertThrows(NonRetryableException.class, actual);
        assertThat(exception.getMessage(), is(equalTo("Client error returned when deleting delta")));
        assertThat(exception.getCause(), is(instanceOf(ApiErrorResponseException.class)));
    }

    @Test
    void testThrowNonRetryableExceptionIfURIValidationExceptionThrown() throws ApiErrorResponseException, URIValidationException {
        // given
        when(internalApiClient.privateDeltaCompanyAppointmentResourceHandler()).thenReturn(resourceHandler);
        when(resourceHandler.deleteCompanyExemptionsResource(any())).thenReturn(deleteHandler);
        when(deleteHandler.execute()).thenThrow(URIValidationException.class);
        Request request = new Request("invalid");
        Client client = new Client(() -> internalApiClient);

        // when
        Executable actual = () -> client.delete(request);

        // then
        NonRetryableException exception = assertThrows(NonRetryableException.class, actual);
        assertThat(exception.getMessage(), is(equalTo("Invalid path specified")));
        assertThat(exception.getCause(), is(instanceOf(URIValidationException.class)));
    }

    @Test
    void testThrowRetryableExceptionIfServerErrorReturned() throws ApiErrorResponseException, URIValidationException {
        // given
        when(internalApiClient.privateDeltaCompanyAppointmentResourceHandler()).thenReturn(resourceHandler);
        when(resourceHandler.deleteCompanyExemptionsResource(any())).thenReturn(deleteHandler);
        when(deleteHandler.execute()).thenThrow(new ApiErrorResponseException(new HttpResponseException.Builder(503, "Service unavailable", new HttpHeaders())));
        Request request = new Request(REQUEST_PATH);
        Client client = new Client(() -> internalApiClient);

        // when
        Executable actual = () -> client.delete(request);

        // then
        RetryableException exception = assertThrows(RetryableException.class, actual);
        assertThat(exception.getMessage(), is(equalTo("Server error returned when deleting delta")));
        assertThat(exception.getCause(), is(instanceOf(ApiErrorResponseException.class)));
    }
}
