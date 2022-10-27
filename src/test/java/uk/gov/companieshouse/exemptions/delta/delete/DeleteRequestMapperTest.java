package uk.gov.companieshouse.exemptions.delta.delete;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.companieshouse.api.delta.PscExemptionDeleteDelta;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest(classes = DeleteRequestMapperImpl.class)
public class DeleteRequestMapperTest {

    @Autowired
    private DeleteRequestMapper requestMapper;

    @Test
    void testMapPscExemptionDeleteDeltaToRequest() {
        // given
        PscExemptionDeleteDelta delta = new PscExemptionDeleteDelta();
        delta.setAction(PscExemptionDeleteDelta.ActionEnum.DELETE);
        delta.setCompanyNumber("12345678");

        // when
        DeleteRequest request = requestMapper.mapDelta(delta);

        // then
        assertThat(request, is(equalTo(new DeleteRequest("/company-exemptions/12345678/internal"))));
    }
}
