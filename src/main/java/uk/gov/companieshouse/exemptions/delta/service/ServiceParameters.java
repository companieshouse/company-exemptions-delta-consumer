package uk.gov.companieshouse.exemptions.delta.service;

import java.util.Objects;
import uk.gov.companieshouse.delta.ChsDelta;

/**
 * Contains all parameters required by {@link Service service implementations}.
 */
public class ServiceParameters {

    private final ChsDelta delta;

    public ServiceParameters(ChsDelta delta) {
        this.delta = delta;
    }

    public ChsDelta getDelta() {
        return delta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServiceParameters)) {
            return false;
        }
        ServiceParameters that = (ServiceParameters) o;
        return Objects.equals(getDelta(), that.getDelta());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDelta());
    }
}
