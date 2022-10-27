package uk.gov.companieshouse.exemptions.delta.upsert;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.delta.PscExemptionDelta;
import uk.gov.companieshouse.delta.ChsDelta;
import uk.gov.companieshouse.exemptions.delta.ServiceParameters;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrchestratorTest {

    @InjectMocks
    private UpsertOrchestrator upsertService;

    @Mock
    private UpsertClient upsertClient;

    @Mock
    private UpsertContentFilter contentFilter;

    @Mock
    private UpsertRequestMapper mapper;

    @Mock
    private ServiceParameters parameters;

    @Mock
    private ChsDelta delta;

    @Mock
    private PscExemptionDelta data;

    @Mock
    private UpsertRequest upsertRequest;

    @Test
    void testProcessMessageUpsertsCompanyExemptionsResource() {
        // given
        when(parameters.getDelta()).thenReturn(delta);
        when(contentFilter.filter(delta)).thenReturn(data);
        when(mapper.mapDelta(data)).thenReturn(upsertRequest);

        // when
        upsertService.processMessage(parameters);

        // then
        verify(contentFilter).filter(delta);
        verify(mapper).mapDelta(data);
        verify(upsertClient).upsert(upsertRequest);
    }
}
