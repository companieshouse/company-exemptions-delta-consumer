package uk.gov.companieshouse.exemptions.delta.delete;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import uk.gov.companieshouse.api.delta.PscExemptionDeleteDelta;

@SpringBootTest(classes = DeleteRequestMapperImpl.class)
@ExtendWith(SpringExtension.class)
public class DeleteRequestMapperTest {

    @Autowired
    private DeleteRequestMapper requestMapper;

    @Test
    void testMapPscExemptionDeleteDeltaToRequest() {
        // given
        PscExemptionDeleteDelta delta = new PscExemptionDeleteDelta();
        delta.setAction(PscExemptionDeleteDelta.ActionEnum.DELETE);
        delta.setCompanyNumber("12345678");
        delta.setDeltaAt("20190612181928152002");

        // when
        DeleteRequest request = requestMapper.mapDelta(delta);

        // then
        assertThat(request, is(equalTo(new DeleteRequest("/company-exemptions/12345678/internal",
                "20190612181928152002"))));
    }
}
