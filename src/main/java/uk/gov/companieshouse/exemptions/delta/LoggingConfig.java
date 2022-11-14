package uk.gov.companieshouse.exemptions.delta;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

/**
 * Configuration class for logging.
 */
@Configuration
public class LoggingConfig {

    private static Logger staticLogger;

    @Bean
    public Logger configLogger() {
        Logger loggerBean = LoggerFactory.getLogger(Application.NAMESPACE);
        staticLogger = loggerBean;
        return loggerBean;
    }

    public static Logger getLogger() {
        return staticLogger;
    }
}