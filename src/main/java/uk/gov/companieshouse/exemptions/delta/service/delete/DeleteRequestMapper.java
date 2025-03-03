package uk.gov.companieshouse.exemptions.delta.service.delete;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import uk.gov.companieshouse.api.delta.PscExemptionDeleteDelta;

/**
 * Maps an {@link PscExemptionDeleteDelta exemption delta} to a {@link DeleteRequest request object} containing parameters
 * required to delete a company exemption resource.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DeleteRequestMapper {

    /**
     * Map an {@link PscExemptionDeleteDelta exemption delete delta} to a {@link DeleteRequest request object}.
     *
     * @param delta The {@link PscExemptionDeleteDelta exemption delete delta} that will be mapped.
     * @return A {@link DeleteRequest request object} containing parameters required to delete a company exemption resource.
     */
    @Mapping(target = "path", ignore = true)
    DeleteRequest mapDelta(PscExemptionDeleteDelta delta);

    @AfterMapping
    default void postMappings(@MappingTarget DeleteRequest request, PscExemptionDeleteDelta delta) {
        request.setPath(String.format("/company-exemptions/%s/internal", delta.getCompanyNumber()));
        request.setDeltaAt(delta.getDeltaAt());
    }
}
