package uk.gov.companieshouse.exemptions.delta;

public interface Service {
    ServiceResult processMessage(ServiceParameters parameters);
}
