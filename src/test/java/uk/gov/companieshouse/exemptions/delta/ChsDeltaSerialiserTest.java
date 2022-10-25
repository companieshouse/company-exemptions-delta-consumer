package uk.gov.companieshouse.exemptions.delta;

import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.delta.ChsDelta;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class ChsDeltaSerialiserTest {

    @Test
    void serialiseChsDelta() {
        // given
        ChsDelta delta = new ChsDelta("{}", 0, "context_id", false);
        ChsDeltaSerialiser serialiser = new ChsDeltaSerialiser();

        // when
        byte[] actual = serialiser.serialize("topic", delta);

        // then
        assertThat(actual, is(notNullValue()));
    }
}
