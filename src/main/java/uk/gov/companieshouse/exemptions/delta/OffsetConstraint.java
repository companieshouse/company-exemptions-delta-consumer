package uk.gov.companieshouse.exemptions.delta;

import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
public class OffsetConstraint {

    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public void setOffsetConstraint(Long offsetConstraint) {
        threadLocal.set(offsetConstraint);
    }

    public Long getOffsetConstraint() {
        return threadLocal.get();
    }

    @PreDestroy
    void destroy() {
        threadLocal.remove();
    }
}
