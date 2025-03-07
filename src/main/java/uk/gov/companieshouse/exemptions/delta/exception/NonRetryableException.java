package uk.gov.companieshouse.exemptions.delta.exception;

/**
 * An unrecoverable error has occurred, e.g. due to the service being misconfigured or due to invalid data.
 */
public class NonRetryableException extends RuntimeException {

    private static final long serialVersionUID = 51926521805273984L;

    public NonRetryableException(String message, Throwable cause) {
        super(message, cause);
    }

    public NonRetryableException(String message) {
        super(message);
    }
}
