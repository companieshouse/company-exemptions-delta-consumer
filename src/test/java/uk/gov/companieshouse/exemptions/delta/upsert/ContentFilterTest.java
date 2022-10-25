package uk.gov.companieshouse.exemptions.delta.upsert;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import uk.gov.companieshouse.api.delta.PscExemptionDelta;
import uk.gov.companieshouse.delta.ChsDelta;
import uk.gov.companieshouse.exemptions.delta.NonRetryableException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ContentFilterTest {

    @Test
    void testExtractPscExemptionFromChsDelta() {
        // given
        ContentFilter filter = new ContentFilter(new ObjectMapper());

        // when
        PscExemptionDelta actual = filter.filter(new ChsDelta("{}", 0, "context_id", false));

        // then
        assertThat(actual, is(equalTo(new PscExemptionDelta())));
    }

    @Test
    void testThrowNonRetryableExceptionIfJsonMalformed() {
        // given
        ContentFilter filter = new ContentFilter(new ObjectMapper());

        // when
        Executable actual = () -> filter.filter(new ChsDelta("invalid", 0, "context_id", false));

        // then
        assertThrows(NonRetryableException.class, actual);
    }
}
