package uk.gov.companieshouse.exemptions.delta.upsert;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.companieshouse.api.delta.ExemptionDates;
import uk.gov.companieshouse.api.delta.PscExemption;
import uk.gov.companieshouse.api.delta.PscExemptionDelta;
import uk.gov.companieshouse.api.delta.PscExemptionDeltaExemption;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.companieshouse.api.exemptions.DiclosureTransparencyRulesChapterFiveAppliesItem.ExemptionTypeEnum.DISCLOSURE_TRANSPARENCY_RULES_CHAPTER_FIVE_APPLIES;

@SpringBootTest(classes = RequestMapperImpl.class)
public class RequestMapperTest {

    @Autowired
    private RequestMapper requestMapper;

    @Test
    void testRequestMapper() {
        // given
        PscExemptionDeltaExemption exemptions = new PscExemptionDeltaExemption();
        PscExemptionDelta delta = new PscExemptionDelta();
        PscExemption exemption = new PscExemption();
        ExemptionDates dates = new ExemptionDates();
        delta.setExemption(exemptions);
        delta.setDeltaAt("20200101000000000000");
        delta.setCompanyNumber("12345678");
        exemptions.setDisclosureTransparencyRulesChapterFiveApplies(exemption);
        exemption.setItems(Collections.singletonList(dates));
        dates.setExemptFrom("20200101");
        dates.setExemptTo("20211231");

        // when
        Request request = requestMapper.mapDelta(delta);

        // then
        assertThat(request.getPath(), is(equalTo("/company-exemptions/12345678/internal")));
        assertThat(request.getBody().getInternalData().getDeltaAt(), is(equalTo(OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC))));
        assertThat(request.getBody().getExternalData().getExemptions().getDisclosureTransparencyRulesChapterFiveApplies().getExemptionType(), is(DISCLOSURE_TRANSPARENCY_RULES_CHAPTER_FIVE_APPLIES));
        assertThat(request.getBody().getExternalData().getExemptions().getDisclosureTransparencyRulesChapterFiveApplies().getItems().get(0).getExemptFrom(), is(LocalDate.of(2020, 1, 1)));
        assertThat(request.getBody().getExternalData().getExemptions().getDisclosureTransparencyRulesChapterFiveApplies().getItems().get(0).getExemptTo(), is(LocalDate.of(2021, 12, 31)));
    }
}
