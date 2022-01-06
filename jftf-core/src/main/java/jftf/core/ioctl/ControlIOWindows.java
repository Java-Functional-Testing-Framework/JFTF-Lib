package jftf.core.ioctl;

public final class ControlIOWindows extends ControlIO{
    private static final String jftfHomeDirectoryNameWindows = "jftf";

    public ControlIOWindows(String configLoggerGroup) {
        super.jftfHomeDirectoryName = jftfHomeDirectoryNameWindows;
        super.attachedConfigLoggerGroup = configLoggerGroup;
        super.setupEnvironment();
    }
}
