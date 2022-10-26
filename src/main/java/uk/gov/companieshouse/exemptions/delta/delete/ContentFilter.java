package uk.gov.companieshouse.exemptions.delta.delete;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.gov.companieshouse.api.delta.PscExemptionDeleteDelta;
import uk.gov.companieshouse.delta.ChsDelta;
import uk.gov.companieshouse.exemptions.delta.NonRetryableException;

public class ContentFilter {

    private ObjectMapper objectMapper;

    public ContentFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public PscExemptionDeleteDelta filter(ChsDelta delta) {
        try {
            return objectMapper.readValue(delta.getData(), PscExemptionDeleteDelta.class);
        } catch (JsonProcessingException e) {
            throw new NonRetryableException("Error extracting exemption delete delta", e);
        }
    }
}
