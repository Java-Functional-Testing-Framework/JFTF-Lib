package jftf.core;

import jftf.core.ioctl.ControlIO;
import jftf.core.ioctl.ControlIOFactory;
import jftf.core.ioctl.DatabaseDriver;
import jftf.core.logging.LoggingContextInformation;
import jftf.core.logging.LoggingController;

public abstract class JftfModule {
    protected static LoggingController logger = null;
    protected static ControlIO controlIO = null;
    protected static DatabaseDriver databaseDriver = null;
    protected LoggingContextInformation attachedLoggingContextInformation = null;

    protected final void attachLogger(LoggingController loggerInstance) {
        if (logger == null && controlIO != null) {
            logger = loggerInstance;
        }
    }

    protected final void attachControlIO(ControlIO controlIOInstance) {
        if (controlIO == null) {
            controlIO = controlIOInstance;
        }
    }

    protected final void attachDatabaseDriver(DatabaseDriver databaseDriverInstance) {
        if (databaseDriver == null) {
            databaseDriver = databaseDriverInstance;
        }
    }

    public final void attachLoggingContextInformation(LoggingContextInformation loggingContextInformation) {
        attachedLoggingContextInformation = loggingContextInformation;
    }

    public static void startupSequence(String configLoggerGroup) {
        if (controlIO == null) {
            ControlIOFactory.getControlIO(configLoggerGroup).checkJftfEnvironmentIntegrity();
            logger.LogInfo("Startup sequence is complete!");
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.LogDebug("Processing shutdown hook");
                if (databaseDriver != null) {
                    databaseDriver.closeConnection();
                }
                logger.LogDebug("Shutdown hook complete!");
            }));
        }
    }
}
