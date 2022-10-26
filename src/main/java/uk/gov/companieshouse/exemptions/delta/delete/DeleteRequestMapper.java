package uk.gov.companieshouse.exemptions.delta.delete;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import uk.gov.companieshouse.api.delta.PscExemptionDeleteDelta;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DeleteRequestMapper {

    @Mapping(target = "path", ignore = true)
    Request mapDelta(PscExemptionDeleteDelta delta);

    @AfterMapping
    default void mapPath(@MappingTarget Request request, PscExemptionDeleteDelta delta) {
        request.setPath(String.format("/company-exemptions/%s/internal", delta.getCompanyNumber()));
    }
}
