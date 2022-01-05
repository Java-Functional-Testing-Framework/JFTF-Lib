package jftf.core.ioctl;

import jftf.core.logging.LoggingContextInformation;

public final class ControlIOWindows extends ControlIO{
    private static final String jftfHomeDirectoryNameWindows = "jftf";

    public ControlIOWindows(LoggingContextInformation loggingContextInformation) {
        super.jftfHomeDirectoryName = jftfHomeDirectoryNameWindows;
        super.attachedLoggingContextInformation = loggingContextInformation;
        super.setupEnvironment();
    }
}
