package uk.gov.companieshouse.exemptions.delta.exception;

/**
 * A recoverable exception has occurred e.g. due to a service that is temporarily unavailable.
 */
public class RetryableException extends RuntimeException {

    private static final long serialVersionUID = 5932496698060923804L;

    public RetryableException(String message, Throwable cause) {
        super(message, cause);
    }
}
