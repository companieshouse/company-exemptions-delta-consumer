package uk.gov.companieshouse.exemptions.delta.delete;

import java.util.Objects;

public class Request {

    private String path;

    public Request(String path) {
        this.path = path;
    }

    public Request() {
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
        Request request = (Request) o;
        return Objects.equals(getPath(), request.getPath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPath());
    }
}
