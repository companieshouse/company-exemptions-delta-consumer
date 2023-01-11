package uk.gov.companieshouse.exemptions.delta;

/**
 * Exception to handle when an invalid payload is sent to the kafka topic.
 */
public class InvalidPayloadException extends RuntimeException {

    public InvalidPayloadException(String message, Throwable cause) {
        super(message, cause);
    }
}
