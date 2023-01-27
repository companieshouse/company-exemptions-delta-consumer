package uk.gov.companieshouse.exemptions.delta;

/**
 * An unrecoverable error has occurred, e.g. due to the service being misconfigured or due to invalid data.
 */
public class NonRetryableException extends RuntimeException {

    public NonRetryableException(String message, Throwable cause) {
        super(message, cause);
    }

    public NonRetryableException(String message) {
        super(message);
    }
}
