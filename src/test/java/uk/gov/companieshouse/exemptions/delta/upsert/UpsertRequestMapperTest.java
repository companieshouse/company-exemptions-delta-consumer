package uk.gov.companieshouse.exemptions.delta.upsert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.companieshouse.api.delta.PscExemptionDelta;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

@SpringBootTest(classes = UpsertRequestMapperImpl.class)
@DisplayName("Upsert request mapper")
public class UpsertRequestMapperTest {

    @Autowired
    private UpsertRequestMapper requestMapper;

    @ParameterizedTest(name = "{1}")
    @MethodSource("examples")
    @DisplayName("Map a PscExemptionDelta to a DeleteRequest")
    void testRequestMapper(String feature, String description) throws IOException, JSONException {
        // given
        String input = IOUtils.resourceToString("/examples/" + feature + "/input.json", StandardCharsets.UTF_8);
        String expected = IOUtils.resourceToString("/examples/" + feature + "/output.json", StandardCharsets.UTF_8);
        ObjectMapper mapper = new ObjectMapper()
                .setAnnotationIntrospector(new JacksonAnnotationIntrospector())
                .registerModule(new JavaTimeModule())
                .setDateFormat(new StdDateFormat());
        PscExemptionDelta delta = mapper.readValue(input, PscExemptionDelta.class);

        // when
        UpsertRequest request = requestMapper.mapDelta(delta);
        String actual = mapper.writeValueAsString(request.getBody());

        // then
        assertEquals(expected, actual, false);
    }

    static Stream<Arguments> examples() {
        return Stream.of(
                Arguments.of("all_exemption_types", "Map an exemption delta containing all exemption types"),
                Arguments.of("empty_exemption_dates", "Map an exemption delta containing empty exemption dates"),
                Arguments.of("empty_items", "Map an exemption delta containing empty items arrays"),
                Arguments.of("no_exemption_types", "Map an exemption delta containing no exemption types")
        );
    }
}
