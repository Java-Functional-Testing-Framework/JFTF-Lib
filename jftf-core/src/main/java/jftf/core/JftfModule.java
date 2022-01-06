package jftf.core;

import jftf.core.ioctl.ControlIO;
import jftf.core.logging.LoggingContextInformation;
import jftf.core.logging.LoggingController;

public abstract class JftfModule {
    protected static LoggingController logger = null;
    protected static ControlIO controlIO = null;
    protected LoggingContextInformation attachedLoggingContextInformation = null;

    protected void attachLogger(LoggingController loggerInstance){
        if(logger == null && controlIO != null) {
            logger = loggerInstance;
        }
    }

    protected void attachControlIO(ControlIO controlIOInstance){
        if(controlIO == null) {
            controlIO = controlIOInstance;
        }
    }

    public void attachLoggingContextInformation(LoggingContextInformation loggingContextInformation){
        attachedLoggingContextInformation = loggingContextInformation;
    }
}
