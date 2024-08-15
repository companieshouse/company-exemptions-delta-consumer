package uk.gov.companieshouse.exemptions.delta.delete;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.delta.PscExemptionDeleteDelta;
import uk.gov.companieshouse.delta.ChsDelta;
import uk.gov.companieshouse.exemptions.delta.ServiceParameters;

@ExtendWith(MockitoExtension.class)
public class OrchestratorTest {

    @InjectMocks
    private DeleteOrchestrator orchestrator;

    @Mock
    private ServiceParameters parameters;

    @Mock
    private ChsDelta delta;

    @Mock
    private DeleteContentFilter filter;

    @Mock
    private PscExemptionDeleteDelta deleteDelta;

    @Mock
    private DeleteRequest request;

    @Mock
    private DeleteRequestMapper requestMapper;

    @Mock
    private DeleteClient client;

    @Test
    void testProcessMessageDeletesCompanyExemptionsResource() {
        // given
        when(parameters.getDelta()).thenReturn(delta);
        when(filter.filter(any())).thenReturn(deleteDelta);
        when(requestMapper.mapDelta(deleteDelta)).thenReturn(request);

        // when
        orchestrator.processMessage(parameters);

        // then
        verify(filter).filter(delta);
        verify(requestMapper).mapDelta(deleteDelta);
        verify(client).delete(request);
    }
}
