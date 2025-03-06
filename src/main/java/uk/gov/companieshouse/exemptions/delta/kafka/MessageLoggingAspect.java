package uk.gov.companieshouse.exemptions.delta.kafka;

import java.util.HashMap;
import java.util.Map;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.delta.ChsDelta;
import uk.gov.companieshouse.logging.Logger;

/**
 * Logs message details before and after it has been processed by
 * the {@link CompanyExemptionsDeltaConsumer main consumer}.<br>
 * <br>
 * Details that will be logged will include:
 * <ul>
 *     <li>The context ID of the message</li>
 *     <li>The topic the message was consumed from</li>
 *     <li>The partition of the topic the message was consumed from</li>
 *     <li>The offset number of the message</li>
 * </ul>
 */
@Component
@Aspect
public class MessageLoggingAspect {

    private static final String LOG_MESSAGE_RECEIVED = "Processing delta";
    private static final String LOG_MESSAGE_PROCESSED = "Processed delta";

    private final Logger logger;

    public MessageLoggingAspect(Logger logger) {
        this.logger = logger;
    }

    @Before("execution(* uk.gov.companieshouse.exemptions.delta.kafka.CompanyExemptionsDeltaConsumer.consume(..))")
    void logBeforeConsumer(JoinPoint joinPoint) {
        logMessage(LOG_MESSAGE_RECEIVED, (Message<?>)joinPoint.getArgs()[0]);
    }

    @After("execution(* uk.gov.companieshouse.exemptions.delta.kafka.CompanyExemptionsDeltaConsumer.consume(..))")
    void logAfterConsumer(JoinPoint joinPoint) {
        logMessage(LOG_MESSAGE_PROCESSED, (Message<?>)joinPoint.getArgs()[0]);
    }

    private void logMessage(String logMessage, Message<?> incomingMessage) {
        Map<String, Object> logData = new HashMap<>();
        ChsDelta payload = (ChsDelta)incomingMessage.getPayload();
        String topic = (String)incomingMessage.getHeaders().get(KafkaHeaders.RECEIVED_TOPIC);
        Integer partition = (Integer)incomingMessage.getHeaders().get(KafkaHeaders.RECEIVED_PARTITION);
        Long offset = (Long)incomingMessage.getHeaders().get(KafkaHeaders.OFFSET);
        logData.put("contextId", payload.getContextId());
        logData.put("topic", topic);
        logData.put("partition", partition);
        logData.put("offset", offset);
        logger.debug(logMessage, logData);
    }
}
