package uk.gov.companieshouse.exemptions.delta.service.delete;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.delta.PscExemptionDeleteDelta;
import uk.gov.companieshouse.delta.ChsDelta;
import uk.gov.companieshouse.exemptions.delta.exception.NonRetryableException;

/**
 * Extracts and deserialises {@link ChsDelta#getData() attached data} from JSON to a {@link PscExemptionDeleteDelta}.
 */
@Component
class DeleteContentFilter {

    private final ObjectMapper objectMapper;

    DeleteContentFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Extract and deserialises {@link ChsDelta#getData() attached data} from JSON to a {@link PscExemptionDeleteDelta}.
     *
     * @param delta The {@link ChsDelta delta} that was consumed.
     * @return {@link PscExemptionDeleteDelta Data} attached to the delta.
     */
    PscExemptionDeleteDelta filter(ChsDelta delta) {
        try {
            return objectMapper.readValue(delta.getData(), PscExemptionDeleteDelta.class);
        } catch (JsonProcessingException e) {
            throw new NonRetryableException("Error extracting exemption delete delta", e);
        }
    }
}
