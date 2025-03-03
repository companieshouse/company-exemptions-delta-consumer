package uk.gov.companieshouse.exemptions.delta.service.delete;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import uk.gov.companieshouse.api.delta.PscExemptionDeleteDelta;
import uk.gov.companieshouse.delta.ChsDelta;
import uk.gov.companieshouse.exemptions.delta.exception.NonRetryableException;

public class DeleteContentFilterTest {

    @Test
    void testExtractPscExemptionDeleteDeltaFromChsDelta() {
        // given
        ChsDelta delta = new ChsDelta("{\"action\": \"DELETE\", \"company_number\": \"12345678\"}",
                0, "context_id", true);
        DeleteContentFilter filter = new DeleteContentFilter(new ObjectMapper());

        // when
        PscExemptionDeleteDelta data = filter.filter(delta);

        // then
        assertThat(data, is(equalTo(expectedExemptionDeleteDelta())));
    }

    @Test
    void testThrowNonRetryableExceptionIfJsonMalformed() {
        // given
        ChsDelta delta = new ChsDelta("{[",
                0, "context_id", true);
        DeleteContentFilter filter = new DeleteContentFilter(new ObjectMapper());

        // when
        Executable actual = () -> filter.filter(delta);

        // then
        NonRetryableException exception = assertThrows(NonRetryableException.class, actual);
        assertThat(exception.getMessage(), is(equalTo("Error extracting exemption delete delta")));
        assertThat(exception.getCause(), is(instanceOf(JsonProcessingException.class)));
    }

    private PscExemptionDeleteDelta expectedExemptionDeleteDelta() {
        PscExemptionDeleteDelta result = new PscExemptionDeleteDelta();
        result.setAction(PscExemptionDeleteDelta.ActionEnum.DELETE);
        result.setCompanyNumber("12345678");
        return result;
    }
}
