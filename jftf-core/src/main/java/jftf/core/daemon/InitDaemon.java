package jftf.core.daemon;

import jftf.core.ioctl.ControlIO;
import jftf.core.ioctl.ControlIOFactory;
import jftf.core.logging.LoggingContextInformation;

public class InitDaemon {
    public static void main(String[] args){
        LoggingContextInformation jftfDaemonLoggingContextInformation = new LoggingContextInformation("jftfDaemon",LoggingContextInformation.defaultLogLevel,LoggingContextInformation.multiAppender);
        ControlIO ctlio = ControlIOFactory.getControlIO(jftfDaemonLoggingContextInformation);
    }
}
