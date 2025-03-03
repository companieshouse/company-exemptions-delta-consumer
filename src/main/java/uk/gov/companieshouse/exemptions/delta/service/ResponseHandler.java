package uk.gov.companieshouse.exemptions.delta.service;

import static uk.gov.companieshouse.exemptions.delta.CompanyExemptionsDeltaConsumerApplication.NAMESPACE;

import java.util.Arrays;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.exemptions.delta.exception.NonRetryableException;
import uk.gov.companieshouse.exemptions.delta.exception.RetryableException;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Component
public class ResponseHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(NAMESPACE);
    private static final String API_INFO_RESPONSE_MESSAGE = "Call to API failed, status code: %d. %s";
    private static final String API_ERROR_RESPONSE_MESSAGE = "Call to API failed, status code: %d";
    private static final String URI_VALIDATION_EXCEPTION_MESSAGE = "Invalid URI";

    public void handle(ApiErrorResponseException ex) {
        final int statusCode = ex.getStatusCode();
        final HttpStatus httpStatus = HttpStatus.valueOf(ex.getStatusCode());

        if (HttpStatus.CONFLICT.equals(httpStatus) || HttpStatus.BAD_REQUEST.equals(httpStatus)) {
            LOGGER.error(String.format(API_ERROR_RESPONSE_MESSAGE, statusCode), ex);
            throw new NonRetryableException(String.format(API_ERROR_RESPONSE_MESSAGE, statusCode), ex);
        } else {
            LOGGER.info(String.format(API_INFO_RESPONSE_MESSAGE, statusCode, Arrays.toString(ex.getStackTrace())));
            throw new RetryableException(String.format(API_ERROR_RESPONSE_MESSAGE, statusCode), ex);
        }
    }

    public void handle(URIValidationException ex) {
        LOGGER.error(URI_VALIDATION_EXCEPTION_MESSAGE);
        throw new NonRetryableException(URI_VALIDATION_EXCEPTION_MESSAGE, ex);
    }
}
