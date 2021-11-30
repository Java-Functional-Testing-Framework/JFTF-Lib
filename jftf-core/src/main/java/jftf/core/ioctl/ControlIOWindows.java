package jftf.core.ioctl;

import jftf.core.logging.LoggingContextInformation;

public class ControlIOWindows extends ControlIO{
    private static final String jftfHomeDirectoryNameWindows = "jftf";
    public ControlIOWindows(LoggingContextInformation loggingContextInformation) {
        super.jftfHomeDirectoryName = jftfHomeDirectoryNameWindows;
        super.setupEnvironment();
        super.setupLogger(loggingContextInformation);
    }
}
