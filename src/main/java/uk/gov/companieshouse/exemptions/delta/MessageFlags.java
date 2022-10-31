package uk.gov.companieshouse.exemptions.delta;

import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
public class MessageFlags {

    private static final ThreadLocal<Boolean> retryableFlagContainer = new ThreadLocal<>();

    public void setRetryable(boolean retryable) {
        retryableFlagContainer.set(retryable);
    }

    public boolean isRetryable() {
        Boolean retryable = retryableFlagContainer.get();
        return retryable != null && retryable;
    }

    @PreDestroy
    public void destroy() {
        retryableFlagContainer.remove();
    }
}
