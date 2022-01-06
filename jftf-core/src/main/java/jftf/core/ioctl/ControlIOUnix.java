package jftf.core.ioctl;

public final class ControlIOUnix extends ControlIO{
    private static final String jftfHomeDirectoryNameUnix = ".jftf";

    public ControlIOUnix(String configLoggerGroup) {
        super.jftfHomeDirectoryName = jftfHomeDirectoryNameUnix;
        super.attachedConfigLoggerGroup = configLoggerGroup;
        super.setupEnvironment();
    }
}
