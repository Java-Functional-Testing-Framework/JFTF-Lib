package jftf.core;

import jftf.core.logging.LoggingContextInformation;
import jftf.core.logging.LoggingController;

public abstract class JftfModule {
    protected static LoggingController logger = null;

    protected void setupLogger(LoggingContextInformation loggingContextInformation){
        logger = LoggingController.LoggerFactory(loggingContextInformation);
    }
}
