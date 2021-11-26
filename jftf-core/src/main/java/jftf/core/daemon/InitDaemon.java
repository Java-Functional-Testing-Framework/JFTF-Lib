package jftf.core.daemon;

import jftf.core.logging.LoggingContextInformation;
import jftf.core.logging.LoggingController;

public class InitDaemon {
    public static void main(String[] args) {
        LoggingController logger = LoggingController.LoggerFactory(new LoggingContextInformation(LoggingContextInformation.defaultLogLevel,LoggingContextInformation.defaultAppender));
        //logger.LogToMinimumLogLevel("dada");
        logger.switchToFaultMode(new Exception(""));
    }
}
