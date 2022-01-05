package jftf.core.ioctl;

import jftf.core.logging.LoggingContextInformation;

public final class ControlIOFactory {
    private static ControlIO controlIO = null;

    public static ControlIO getControlIO(LoggingContextInformation loggingContextInformation){
        if(OsUtils.isLinux()){
            if(controlIO == null)
                controlIO = new ControlIOUnix(loggingContextInformation);
            return controlIO;
        }
        else if(OsUtils.isWindows()){
            if(controlIO == null)
                controlIO = new ControlIOWindows(loggingContextInformation);
            return controlIO;
        }
        System.err.println("(CRITICAL) Operating system is not supported!");
        System.exit(2);
        return null;
    }

    public static ControlIO getControlIO(){
        if(controlIO != null){
            return controlIO;
        }
        return  null;
    }
}
