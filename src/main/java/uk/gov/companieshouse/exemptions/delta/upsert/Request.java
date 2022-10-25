package uk.gov.companieshouse.exemptions.delta.upsert;

import uk.gov.companieshouse.api.exemptions.InternalExemptionsApi;

import java.util.Objects;

public class Request {

    private InternalExemptionsApi body;
    private String path;

    public InternalExemptionsApi getBody() {
        return body;
    }

    public void setBody(InternalExemptionsApi body) {
        this.body = body;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Request)) {
            return false;
        }
        Request that = (Request) o;
        return Objects.equals(getBody(), that.getBody()) && Objects.equals(getPath(), that.getPath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBody(), getPath());
    }
}
