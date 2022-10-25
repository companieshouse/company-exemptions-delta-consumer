package uk.gov.companieshouse.exemptions.delta.upsert;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import uk.gov.companieshouse.api.delta.PscExemptionDelta;
import uk.gov.companieshouse.api.exemptions.InternalData;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface RequestMapper {

    DateTimeFormatter EXEMPTION_DATE_PATTERN = DateTimeFormatter.ofPattern("yyyyMMdd");
    DateTimeFormatter DELTA_TIME_PATTERN = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSSSSS")
            .withZone(ZoneId.of("Z"));

    @Mapping(source = "exemption", target = "body.externalData.exemptions")
    @Mapping(constant = "PSC_EXEMPT_AS_TRADING_ON_REGULATED_MARKET", target = "body.externalData.exemptions.pscExemptAsTradingOnRegulatedMarket.exemptionType")
    @Mapping(constant = "PSC_EXEMPT_AS_SHARES_ADMITTED_ON_MARKET", target = "body.externalData.exemptions.pscExemptAsSharesAdmittedOnMarket.exemptionType")
    @Mapping(constant = "PSC_EXEMPT_AS_TRADING_ON_UK_REGULATED_MARKET", target = "body.externalData.exemptions.pscExemptAsTradingOnUkRegulatedMarket.exemptionType")
    @Mapping(constant = "PSC_EXEMPT_AS_TRADING_ON_EU_REGULATED_MARKET", target = "body.externalData.exemptions.pscExemptAsTradingOnEuRegulatedMarket.exemptionType")
    @Mapping(constant = "DISCLOSURE_TRANSPARENCY_RULES_CHAPTER_FIVE_APPLIES", target = "body.externalData.exemptions.disclosureTransparencyRulesChapterFiveApplies.exemptionType")
    @Mapping(target = "path", ignore = true)
    @Mapping(target = "body.internalData.deltaAt", ignore = true)
    Request mapDelta(PscExemptionDelta delta);

    default LocalDate stringToLocalDate(String dateString) {
        if (dateString == null) {
            return null;
        }
        return LocalDate.parse(dateString, EXEMPTION_DATE_PATTERN);
    }

    @AfterMapping
    default void mapPath(@MappingTarget Request request, PscExemptionDelta delta) {
        request.setPath(String.format("/company-exemptions/%s/internal", delta.getCompanyNumber()));
    }

    @AfterMapping
    default void parseDeltaAt(@MappingTarget InternalData internalData, PscExemptionDelta delta) {
        internalData.setDeltaAt(ZonedDateTime.parse(delta.getDeltaAt(), DELTA_TIME_PATTERN)
                .toOffsetDateTime());
    }
}
