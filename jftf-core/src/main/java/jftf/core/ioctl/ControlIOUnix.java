package jftf.core.ioctl;

public final class ControlIOUnix extends ControlIO{
    private static final String applicationDirectoryNameUnix = ".jftf";
    public ControlIOUnix() {
        super.applicationDirectoryName = applicationDirectoryNameUnix;
        super.generateBaseDirectory();
    }
}
