package uk.gov.companieshouse.exemptions.delta.service.upsert;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponseException;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.exemptions.InternalExemptionsApi;
import uk.gov.companieshouse.api.handler.delta.PrivateDeltaResourceHandler;
import uk.gov.companieshouse.api.handler.delta.exemptions.request.PrivateCompanyExemptionsUpsert;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.http.HttpClient;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.exemptions.delta.service.ResponseHandler;

@ExtendWith(MockitoExtension.class)
class UpsertClientTest {

    @Mock
    private InternalApiClient internalApiClient;

    @Mock
    private ResponseHandler handler;

    @Mock
    private PrivateDeltaResourceHandler deltaResourceHandler;

    @Mock
    private PrivateCompanyExemptionsUpsert exemptionsUpsertHandler;

    @Mock
    private HttpClient httpClient;

    @Test
    void testUpsert() throws ApiErrorResponseException, URIValidationException {
        // given
        InternalExemptionsApi body = new InternalExemptionsApi();
        UpsertRequest request = new UpsertRequest();
        request.setPath("/company-exemptions/12345678/internal");
        request.setBody(body);
        when(internalApiClient.privateDeltaCompanyAppointmentResourceHandler()).thenReturn(deltaResourceHandler);
        when(internalApiClient.getHttpClient()).thenReturn(httpClient);
        when(deltaResourceHandler.upsertCompanyExemptionsResource(anyString(), any())).thenReturn(exemptionsUpsertHandler);
        when(exemptionsUpsertHandler.execute()).thenReturn(new ApiResponse<>(200, Collections.emptyMap()));
        UpsertClient client = new UpsertClient(() -> internalApiClient, handler);

        // when
        client.upsert(request);

        // then
        verify(deltaResourceHandler).upsertCompanyExemptionsResource("/company-exemptions/12345678/internal", body);
        verify(exemptionsUpsertHandler).execute();
    }

    @Test
    void testUpsertHandles404NotFoundErrorResponse() throws ApiErrorResponseException, URIValidationException {
        ApiErrorResponseException exception = new ApiErrorResponseException(
                new HttpResponseException.Builder(404, "Not found", new HttpHeaders()));

        // given
        InternalExemptionsApi body = new InternalExemptionsApi();
        UpsertRequest request = new UpsertRequest();
        request.setPath("/company-exemptions/12345678/internal");
        request.setBody(body);
        when(internalApiClient.privateDeltaCompanyAppointmentResourceHandler()).thenReturn(deltaResourceHandler);
        when(internalApiClient.getHttpClient()).thenReturn(httpClient);
        when(deltaResourceHandler.upsertCompanyExemptionsResource(anyString(), any())).thenReturn(exemptionsUpsertHandler);
        when(exemptionsUpsertHandler.execute()).thenThrow(exception);
        UpsertClient client = new UpsertClient(() -> internalApiClient, handler);

        // when
        client.upsert(request);

        // then
        verify(internalApiClient).privateDeltaCompanyAppointmentResourceHandler();
        verify(deltaResourceHandler).upsertCompanyExemptionsResource(anyString(), any());
        verify(exemptionsUpsertHandler).execute();
        verify(handler).handle(exception);
    }

    @Test
    void testUpsertHandles500InternalServerErrorResponse() throws ApiErrorResponseException, URIValidationException {
        ApiErrorResponseException exception = new ApiErrorResponseException(new HttpResponseException.Builder(
                500, "Internal server error", new HttpHeaders()));

        // given
        InternalExemptionsApi body = new InternalExemptionsApi();
        UpsertRequest request = new UpsertRequest();
        request.setPath("/company-exemptions/12345678/internal");
        request.setBody(body);
        when(internalApiClient.privateDeltaCompanyAppointmentResourceHandler()).thenReturn(deltaResourceHandler);
        when(internalApiClient.getHttpClient()).thenReturn(httpClient);
        when(deltaResourceHandler.upsertCompanyExemptionsResource(anyString(), any())).thenReturn(exemptionsUpsertHandler);
        when(exemptionsUpsertHandler.execute()).thenThrow(exception);
        UpsertClient client = new UpsertClient(() -> internalApiClient, handler);

        // when
        client.upsert(request);

        // then
        verify(internalApiClient).privateDeltaCompanyAppointmentResourceHandler();
        verify(deltaResourceHandler).upsertCompanyExemptionsResource(anyString(), any());
        verify(exemptionsUpsertHandler).execute();
        verify(handler).handle(exception);
    }

    @Test
    void testThrowNonRetryableExceptionIfPathInvalid() throws ApiErrorResponseException, URIValidationException {
        Class<URIValidationException> exceptionClass = URIValidationException.class;

        // given
        InternalExemptionsApi body = new InternalExemptionsApi();
        UpsertRequest request = new UpsertRequest();
        request.setPath("/invalid/path");
        request.setBody(body);
        when(internalApiClient.privateDeltaCompanyAppointmentResourceHandler()).thenReturn(deltaResourceHandler);
        when(internalApiClient.getHttpClient()).thenReturn(httpClient);
        when(deltaResourceHandler.upsertCompanyExemptionsResource(anyString(), any())).thenReturn(exemptionsUpsertHandler);
        when(exemptionsUpsertHandler.execute()).thenThrow(exceptionClass);
        UpsertClient client = new UpsertClient(() -> internalApiClient, handler);

        // when
        client.upsert(request);

        // then
        verify(internalApiClient).privateDeltaCompanyAppointmentResourceHandler();
        verify(deltaResourceHandler).upsertCompanyExemptionsResource(anyString(), any());
        verify(exemptionsUpsertHandler).execute();
        verify(handler).handle(any(exceptionClass));
    }
}
