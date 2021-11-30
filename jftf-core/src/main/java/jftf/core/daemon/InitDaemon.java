package jftf.core.daemon;

import jftf.core.ioctl.ControlIO;
import jftf.core.ioctl.ControlIOFactory;
import jftf.core.logging.LoggingContextInformation;

public class InitDaemon {
    public static void main(String[] args) throws InterruptedException {
        LoggingContextInformation jftfDaemonLoggingContextInformation = new LoggingContextInformation("jftfDaemon",LoggingContextInformation.defaultLogLevel,LoggingContextInformation.multiAppender);
        ControlIO ctlio = ControlIOFactory.getControlIO(jftfDaemonLoggingContextInformation);
        System.out.println(ctlio.getJftfHomeDirectoryPath());
        System.out.println(ctlio.getJftfLogDirectoryPath());
        System.out.println(ctlio.getJftfTestCasesDirectoryPath());
        System.out.println(ctlio.checkJftfEnvironmentIntegrity());
    }
}
