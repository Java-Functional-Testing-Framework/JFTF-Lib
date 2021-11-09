package jftf.core.logging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.LoggerContext;

public class LoggingController {
    private final Logger LOGGER;
    private final LoggerContext currentLoggerContext;
    public LoggingController(LoggingContextInformation currentLoggerContextInformation) {
        try {
            this.LOGGER = LoggerFactory.getLogger(currentLoggerContextInformation.getCurrentApplicationID());
            this.currentLoggerContext = (LoggerContext) this.LOGGER;
        }
        catch(Exception e){
            throw new LoggerFailedInitialization(String.format("Logging context for application ID: '%s' failed to initialize!",currentLoggerContextInformation.getCurrentApplicationID()),e);
        }
        this.LOGGER.info(String.format("Registered new logging context for application ID: '%s'", currentLoggerContextInformation.getCurrentApplicationID()));
    }
    public Logger exposeLogger(){
        return this.LOGGER;
    }
}
