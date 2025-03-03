package uk.gov.companieshouse.exemptions.delta.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.exemptions.delta.exception.NonRetryableException;
import uk.gov.companieshouse.exemptions.delta.exception.RetryableException;

@ExtendWith(MockitoExtension.class)
class ResponseHandlerTest {

    private final ResponseHandler responseHandler = new ResponseHandler();

    @Mock
    private ApiErrorResponseException apiErrorResponseException;

    @Mock
    private URIValidationException uriValidationException;

    @ParameterizedTest
    @CsvSource({
            "401",
            "403",
            "404",
            "405",
            "500",
            "501",
            "502",
            "503",
            "504"
    })
    void shouldHandleApiErrorResponseByThrowingRetryableExceptionForSpecificStatusCodes(final int httpStatusCode) {
        // given
        when(apiErrorResponseException.getStatusCode()).thenReturn(httpStatusCode);

        // when
        Executable executable = () -> responseHandler.handle(apiErrorResponseException);

        // then
        assertThrows(RetryableException.class, executable);
    }

    @ParameterizedTest
    @CsvSource({
            "400",
            "409"
    })
    void shouldHandleApiErrorResponseByThrowingNonRetryableExceptionFor400And409(final int httpStatusCode) {
        // given
        when(apiErrorResponseException.getStatusCode()).thenReturn(httpStatusCode);

        // when
        Executable executable = () -> responseHandler.handle(apiErrorResponseException);

        // then
        assertThrows(NonRetryableException.class, executable);
    }

    @Test
    void shouldHandleUriValidationExceptionByThrowingNonRetryableException() {
        // given

        // when
        Executable executable = () -> responseHandler.handle(uriValidationException);

        // then
        assertThrows(NonRetryableException.class, executable);
    }
}
