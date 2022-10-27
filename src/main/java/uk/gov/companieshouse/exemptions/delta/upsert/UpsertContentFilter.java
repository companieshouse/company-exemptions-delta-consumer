package uk.gov.companieshouse.exemptions.delta.upsert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.delta.PscExemptionDelta;
import uk.gov.companieshouse.delta.ChsDelta;
import uk.gov.companieshouse.exemptions.delta.NonRetryableException;

/**
 * Extracts and deserialises {@link ChsDelta#getData() data} attached from JSON to a {@link PscExemptionDelta}.
 */
@Component
class UpsertContentFilter {

    private final ObjectMapper mapper;

    UpsertContentFilter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * Extract and deserialises {@link ChsDelta#getData() data} attached from JSON to a {@link PscExemptionDelta}.
     *
     * @param delta The {@link ChsDelta delta} that was consumed.
     * @return {@link PscExemptionDelta Data} attached to the delta.
     */
    PscExemptionDelta filter(ChsDelta delta) {
        try {
            return mapper.readValue(delta.getData(), PscExemptionDelta.class);
        } catch (JsonProcessingException e) {
            throw new NonRetryableException("Error extracting exemption delta", e);
        }
    }
}
