package jftf.core.ioctl;

import jftf.core.logging.LoggingContextInformation;

public final class ControlIOFactory {
    static private ControlIO ctlioInstance = null;
    public static ControlIO getControlIO(LoggingContextInformation loggingContextInformation){
        if(OsUtils.isLinux()){
            if(ctlioInstance == null)
                ctlioInstance = new ControlIOUnix(loggingContextInformation);
            return ctlioInstance;
        }
        else if(OsUtils.isWindows()){
            if(ctlioInstance == null)
                ctlioInstance = new ControlIOWindows(loggingContextInformation);
            return ctlioInstance;
        }
        System.err.println("(CRITICAL) Operating system is not supported!");
        System.exit(3);
        return null;
    }
}
