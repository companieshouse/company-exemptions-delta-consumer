package uk.gov.companieshouse.exemptions.delta.kafka;

import static uk.gov.companieshouse.exemptions.delta.CompanyExemptionsDeltaConsumerApplication.NAMESPACE;

import java.util.Optional;
import java.util.UUID;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.delta.ChsDelta;
import uk.gov.companieshouse.exemptions.delta.logging.DataMapHolder;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

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
    private static final Logger LOGGER = LoggerFactory.getLogger(NAMESPACE);

    @Before("execution(* uk.gov.companieshouse.exemptions.delta.kafka.CompanyExemptionsDeltaConsumer.consume(..))")
    void logBeforeConsumer(JoinPoint joinPoint) {
        Message<?> incomingMessage = (Message<?>)joinPoint.getArgs()[0];
        ChsDelta payload = (ChsDelta)incomingMessage.getPayload();

        DataMapHolder.initialise(Optional.ofNullable(payload.getContextId())
                .orElse(UUID.randomUUID().toString()));

        String topic = (String)incomingMessage.getHeaders().get(KafkaHeaders.RECEIVED_TOPIC);
        Integer partition = (Integer)incomingMessage.getHeaders().get(KafkaHeaders.RECEIVED_PARTITION);
        Long offset = (Long)incomingMessage.getHeaders().get(KafkaHeaders.OFFSET);

        DataMapHolder.get()
                .topic(topic)
                .partition(partition)
                .offset(offset);

        LOGGER.info(LOG_MESSAGE_RECEIVED, DataMapHolder.getLogMap());
    }

    @After("execution(* uk.gov.companieshouse.exemptions.delta.kafka.CompanyExemptionsDeltaConsumer.consume(..))")
    void logAfterConsumer(JoinPoint joinPoint) {
        LOGGER.info(LOG_MESSAGE_PROCESSED, DataMapHolder.getLogMap());
        DataMapHolder.clear();
    }
}
