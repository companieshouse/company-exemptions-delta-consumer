package uk.gov.companieshouse.exemptions.delta;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.delta.ChsDelta;

@ExtendWith(MockitoExtension.class)
public class ServiceRouterTest {

    private ServiceRouter router;

    @Mock
    private Service upsertService;

    @Mock
    private Service deleteService;

    @BeforeEach
    void before() {
        router = new ServiceRouter(upsertService, deleteService);
    }

    @Test
    void testRouteMessageToUpsertServiceIfNonDelete() {
        // given
        ChsDelta data = new ChsDelta("{}", 0, "context_id", false);

        // when
        router.route(data);

        // then
        verify(upsertService).processMessage(new ServiceParameters(data));
    }

    @Test
    void testRouteMessageToDeleteServiceIfDelete() {
        // given
        ChsDelta data = new ChsDelta("{}", 0, "context_id", true);

        // when
        router.route(data);

        // then
        verify(deleteService).processMessage(new ServiceParameters(data));
    }
}
