package uk.gov.companieshouse.exemptions.delta.exception;

/**
 * Exception to handle when an invalid payload is sent to the kafka topic.
 */
public class InvalidPayloadException extends RuntimeException {

    private static final long serialVersionUID = -8906667414301827974L;

    public InvalidPayloadException(String message, Throwable cause) {
        super(message, cause);
    }
}
