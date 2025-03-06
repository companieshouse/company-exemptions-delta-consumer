package uk.gov.companieshouse.exemptions.delta.consumer;

import java.util.concurrent.CountDownLatch;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ConsumerAspect {

    private final int steps;
    private CountDownLatch latch;

    public ConsumerAspect(@Value("${steps:1}") int steps) {
        this.steps = steps;
        this.latch = new CountDownLatch(steps);
    }

    @After("execution(* uk.gov.companieshouse.exemptions.delta.kafka.CompanyExemptionsDeltaConsumer.consume(..))")
    void afterConsume(JoinPoint joinPoint) {
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public void resetLatch() {
        latch = new CountDownLatch(steps);
    }
}
