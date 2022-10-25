package uk.gov.companieshouse.exemptions.delta.upsert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.delta.PscExemptionDelta;
import uk.gov.companieshouse.delta.ChsDelta;
import uk.gov.companieshouse.exemptions.delta.NonRetryableException;

@Component
class ContentFilter {

    private final ObjectMapper mapper;

    ContentFilter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    PscExemptionDelta filter(ChsDelta delta) {
        try {
            return mapper.readValue(delta.getData(), PscExemptionDelta.class);
        } catch (JsonProcessingException e) {
            throw new NonRetryableException("Error extracting exemption delta", e);
        }
    }
}
