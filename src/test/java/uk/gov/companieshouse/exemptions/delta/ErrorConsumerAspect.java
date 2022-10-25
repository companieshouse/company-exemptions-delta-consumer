package uk.gov.companieshouse.exemptions.delta;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Aspect
@Component
public class ErrorConsumerAspect {

    private CountDownLatch latch;

    public ErrorConsumerAspect(CountDownLatch latch) {
        this.latch = latch;
    }

    @After("execution(* uk.gov.companieshouse.exemptions.delta.ErrorConsumer.consume(..))")
    void afterConsume(JoinPoint joinPoint) {
        latch.countDown();
    }

    @After("execution(* uk.gov.companieshouse.exemptions.delta.FixedDestinationResolver.resolve(..))")
    void afterHandleError(JoinPoint joinPoint) {
        latch.countDown();
    }
}
