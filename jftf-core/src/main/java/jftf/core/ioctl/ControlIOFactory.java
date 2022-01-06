package jftf.core.ioctl;

public final class ControlIOFactory {
    private static ControlIO controlIO = null;

    public static ControlIO getControlIO(String configLoggerGroup){
        if(OsUtils.isLinux()){
            if(controlIO == null)
                controlIO = new ControlIOUnix(configLoggerGroup);
            return controlIO;
        }
        else if(OsUtils.isWindows()){
            if(controlIO == null)
                controlIO = new ControlIOWindows(configLoggerGroup);
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
