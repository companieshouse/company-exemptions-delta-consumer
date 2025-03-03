package uk.gov.companieshouse.exemptions.delta.service.upsert;

import java.util.Objects;
import uk.gov.companieshouse.api.exemptions.InternalExemptionsApi;

/**
 * Contains all parameters required to upsert a company exemptions resource.
 */
public class UpsertRequest {

    private InternalExemptionsApi body;
    private String path;

    public UpsertRequest(InternalExemptionsApi body, String path) {
        this.body = body;
        this.path = path;
    }

    public UpsertRequest() {
    }

    /**
     * Retrieve the body that will be sent to the upsert exemptions API.
     *
     * @return An {@link InternalExemptionsApi object} representing the resource that will be upserted.
     */
    public InternalExemptionsApi getBody() {
        return body;
    }

    /**
     * Set the body that will be sent to the upsert exemptions API.
     *
     * @param body An {@link InternalExemptionsApi object} representing the resource that will be upserted.
     */
    public void setBody(InternalExemptionsApi body) {
        this.body = body;
    }

    /**
     * Retrieve the path to which the request will be sent.
     *
     * @return The path to which the request will be sent.
     */
    public String getPath() {
        return path;
    }

    /**
     * Set the path to which the request will be sent.
     *
     * @param path The path to which the request will be sent.
     */
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UpsertRequest)) {
            return false;
        }
        UpsertRequest that = (UpsertRequest) o;
        return Objects.equals(getBody(), that.getBody()) && Objects.equals(getPath(), that.getPath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBody(), getPath());
    }
}
