package jftf.core;

import jftf.core.ioctl.ControlIO;
import jftf.core.ioctl.ControlIOFactory;
import jftf.core.logging.LoggingContextInformation;
import jftf.core.logging.LoggingController;

public abstract class JftfModule {
    protected static LoggingController logger = null;
    protected static ControlIO controlIO = null;

    protected void setupLogger(LoggingContextInformation loggingContextInformation){
        if(logger == null && controlIO != null) {
            logger = LoggingController.LoggerFactory(loggingContextInformation);
        }
    }

    protected void setupControlIO(LoggingContextInformation loggingContextInformation){
        if(controlIO == null) {
            controlIO = ControlIOFactory.getControlIO(loggingContextInformation);
        }
    }
}
