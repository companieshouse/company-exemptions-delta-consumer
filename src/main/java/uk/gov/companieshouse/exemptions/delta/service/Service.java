package uk.gov.companieshouse.exemptions.delta.service;

import uk.gov.companieshouse.exemptions.delta.exception.NonRetryableException;
import uk.gov.companieshouse.exemptions.delta.exception.RetryableException;

/**
 * Processes an incoming ChsDelta message.
 */
public interface Service {

    /**
     * Processes an incoming ChsDelta message.
     *
     * @param parameters Any parameters required when processing the message.
     */
    void processMessage(ServiceParameters parameters) throws RetryableException, NonRetryableException;
}
