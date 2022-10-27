package uk.gov.companieshouse.exemptions.delta.delete;

import java.util.Objects;

/**
 * Contains all parameters required to delete a company exemptions resource.
 */
public class DeleteRequest {

    private String path;

    public DeleteRequest(String path) {
        this.path = path;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DeleteRequest)) {
            return false;
        }
        DeleteRequest request = (DeleteRequest) o;
        return Objects.equals(getPath(), request.getPath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPath());
    }
}
