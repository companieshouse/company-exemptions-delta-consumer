package uk.gov.companieshouse.exemptions.delta.upsert;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponseException;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.exemptions.InternalExemptionsApi;
import uk.gov.companieshouse.api.handler.delta.PrivateDeltaResourceHandler;
import uk.gov.companieshouse.api.handler.delta.exemptions.request.PrivateCompanyExemptionsUpsert;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.exemptions.delta.NonRetryableException;
import uk.gov.companieshouse.exemptions.delta.RetryableException;
import uk.gov.companieshouse.logging.Logger;

@ExtendWith(MockitoExtension.class)
public class ClientTest {

    @Mock
    private InternalApiClient internalApiClient;

    @Mock
    private PrivateDeltaResourceHandler deltaResourceHandler;

    @Mock
    private PrivateCompanyExemptionsUpsert exemptionsUpsertHandler;

    @Mock
    private Logger logger;

    @Test
    void testUpsert() throws ApiErrorResponseException, URIValidationException {
        // given
        InternalExemptionsApi body = new InternalExemptionsApi();
        UpsertRequest request = new UpsertRequest();
        request.setPath("/company-exemptions/12345678/internal");
        request.setBody(body);
        when(internalApiClient.privateDeltaCompanyAppointmentResourceHandler()).thenReturn(deltaResourceHandler);
        when(deltaResourceHandler.upsertCompanyExemptionsResource(anyString(), any())).thenReturn(exemptionsUpsertHandler);
        when(exemptionsUpsertHandler.execute()).thenReturn(new ApiResponse<>(200, Collections.emptyMap()));
        UpsertClient client = new UpsertClient(() -> internalApiClient, logger);

        // when
        client.upsert(request);

        // then
        verify(deltaResourceHandler).upsertCompanyExemptionsResource("/company-exemptions/12345678/internal", body);
        verify(exemptionsUpsertHandler).execute();
    }

    @Test
    void testThrowNonRetryableExceptionIfClientErrorReturned() throws ApiErrorResponseException, URIValidationException {
        // given
        InternalExemptionsApi body = new InternalExemptionsApi();
        UpsertRequest request = new UpsertRequest();
        request.setPath("/company-exemptions/12345678/internal");
        request.setBody(body);
        when(internalApiClient.privateDeltaCompanyAppointmentResourceHandler()).thenReturn(deltaResourceHandler);
        when(deltaResourceHandler.upsertCompanyExemptionsResource(anyString(), any())).thenReturn(exemptionsUpsertHandler);
        when(exemptionsUpsertHandler.execute()).thenThrow(new ApiErrorResponseException(new HttpResponseException.Builder(404, "Not found", new HttpHeaders())));
        UpsertClient client = new UpsertClient(() -> internalApiClient, logger);

        // when
        Executable actual = () -> client.upsert(request);

        // then
        assertThrows(NonRetryableException.class, actual);
        verify(deltaResourceHandler).upsertCompanyExemptionsResource("/company-exemptions/12345678/internal", body);
        verify(exemptionsUpsertHandler).execute();
    }

    @Test
    void testThrowRetryableExceptionIfServerErrorReturned() throws ApiErrorResponseException, URIValidationException {
        // given
        InternalExemptionsApi body = new InternalExemptionsApi();
        UpsertRequest request = new UpsertRequest();
        request.setPath("/company-exemptions/12345678/internal");
        request.setBody(body);
        when(internalApiClient.privateDeltaCompanyAppointmentResourceHandler()).thenReturn(deltaResourceHandler);
        when(deltaResourceHandler.upsertCompanyExemptionsResource(anyString(), any())).thenReturn(exemptionsUpsertHandler);
        when(exemptionsUpsertHandler.execute()).thenThrow(new ApiErrorResponseException(new HttpResponseException.Builder(500, "Internal server error", new HttpHeaders())));
        UpsertClient client = new UpsertClient(() -> internalApiClient, logger);

        // when
        Executable actual = () -> client.upsert(request);

        // then
        assertThrows(RetryableException.class, actual);
        verify(deltaResourceHandler).upsertCompanyExemptionsResource("/company-exemptions/12345678/internal", body);
        verify(exemptionsUpsertHandler).execute();
    }

    @Test
    void testThrowRetryableExceptionIfIllegalArgumentExceptionIsCaught() throws ApiErrorResponseException, URIValidationException {
        // given
        InternalExemptionsApi body = new InternalExemptionsApi();
        UpsertRequest request = new UpsertRequest();
        request.setPath("/company-exemptions/12345678/internal");
        request.setBody(body);
        when(internalApiClient.privateDeltaCompanyAppointmentResourceHandler()).thenReturn(deltaResourceHandler);
        when(deltaResourceHandler.upsertCompanyExemptionsResource(anyString(), any())).thenReturn(exemptionsUpsertHandler);
        when(exemptionsUpsertHandler.execute()).thenThrow(new IllegalArgumentException("Internal server error"));
        UpsertClient client = new UpsertClient(() -> internalApiClient, logger);

        // when
        Executable actual = () -> client.upsert(request);

        // then
        assertThrows(RetryableException.class, actual);
        verify(deltaResourceHandler).upsertCompanyExemptionsResource("/company-exemptions/12345678/internal", body);
        verify(exemptionsUpsertHandler).execute();
    }

    @Test
    void testThrowNonRetryableExceptionIfPathInvalid() throws ApiErrorResponseException, URIValidationException {
        // given
        InternalExemptionsApi body = new InternalExemptionsApi();
        UpsertRequest request = new UpsertRequest();
        request.setPath("/invalid/path");
        request.setBody(body);
        when(internalApiClient.privateDeltaCompanyAppointmentResourceHandler()).thenReturn(deltaResourceHandler);
        when(deltaResourceHandler.upsertCompanyExemptionsResource(anyString(), any())).thenReturn(exemptionsUpsertHandler);
        when(exemptionsUpsertHandler.execute()).thenThrow(new URIValidationException("Invalid URI"));
        UpsertClient client = new UpsertClient(() -> internalApiClient, logger);

        // when
        Executable actual = () -> client.upsert(request);

        // then
        assertThrows(NonRetryableException.class, actual);
        verify(deltaResourceHandler).upsertCompanyExemptionsResource("/invalid/path", body);
        verify(exemptionsUpsertHandler).execute();
    }
}
