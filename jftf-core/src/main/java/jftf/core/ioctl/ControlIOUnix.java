package jftf.core.ioctl;

import jftf.core.logging.LoggingContextInformation;

public final class ControlIOUnix extends ControlIO{
    private static final String jftfHomeDirectoryNameUnix = ".jftf";

    public ControlIOUnix(LoggingContextInformation loggingContextInformation) {
        super.jftfHomeDirectoryName = jftfHomeDirectoryNameUnix;
        super.attachedLoggingContextInformation = loggingContextInformation;
        super.setupEnvironment();
    }
}
