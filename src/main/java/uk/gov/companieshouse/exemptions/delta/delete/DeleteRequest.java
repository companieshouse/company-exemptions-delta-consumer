package uk.gov.companieshouse.exemptions.delta.delete;

import java.util.Objects;

/**
 * Contains all parameters required to delete a company exemptions resource.
 */
public class DeleteRequest {

    private String path;
    private String deltaAt;

    public DeleteRequest(String path, String deltaAt) {
        this.path = path;
        this.deltaAt = deltaAt;
    }

    public DeleteRequest() {
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

    public String getDeltaAt() {
        return deltaAt;
    }

    public void setDeltaAt(String deltaAt) {
        this.deltaAt = deltaAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DeleteRequest that = (DeleteRequest) o;
        return Objects.equals(getPath(), that.getPath()) && Objects.equals(getDeltaAt(),
                that.getDeltaAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPath(), getDeltaAt());
    }
}
