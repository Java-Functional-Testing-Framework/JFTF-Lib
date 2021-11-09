package jftf.core.ioctl;

public final class ControlIOFactory {
    public static ControlIO getControlIO(){
        if(OsUtils.isLinux()){
            return new ControlIOUnix();
        }
        else if(OsUtils.isWindows()){
            return new ControIOWindows();
        }
        return null;
    }
}
