package jftf.core.logging;

import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LoggingControllerContextShiftTesting {
    private LoggingController Logger;
    private File consoleOut = null;
    private InputStream consoleOutIs;
    private final String loggerApplicationID = "LoggerContextShiftTest";
    private final String newContextShiftingInitializationMessage = String.format("Shifting to new logging context information! Log level: '%s' | Appender type: '%s' (INTERNAL / IGNORE)",LoggingContextInformation.errorLogLevel,LoggingContextInformation.consoleAppender);
    private final String newContextShiftingConfirmationMessage = String.format("Shifted to new logging context for application ID: '%s' | Log level: '%s' | Appender type: '%s' (INTERNAL / IGNORE)",loggerApplicationID,LoggingContextInformation.errorLogLevel,LoggingContextInformation.consoleAppender);
    private final String newLogLevelShiftingInitializationMessage = String.format("Shifting log level for logging context: '%s' --> '%s' (INTERNAL / IGNORE)",LoggingContextInformation.defaultLogLevel,LoggingContextInformation.errorLogLevel);
    private final String newLogLevelShiftingConfirmationMessage = String.format("Shifted to --> '%s' Logging level shift confirmation message! (INTERNAL / IGNORE)",LoggingContextInformation.errorLogLevel);
    private final String debugLogLevelCheckMessage = "Debug log message!";
    private final String infoLogLevelCheckMessage = "Info log message!";
    private final String errorLogLevelCheckMessage = "Error log message!";
    private final String expectedDebugLogLevelCheckMessage = String.format("%s %s - Debug log message!",LoggingContextInformation.debugLogLevel,loggerApplicationID);
    private final String expectedInfoLogLevelCheckMessage = String.format("%s  %s - Info log message!",LoggingContextInformation.infoLogLevel,loggerApplicationID);
    private final String expectedErrorLogLevelCheckMessage = String.format("%s %s - Error log message!",LoggingContextInformation.errorLogLevel,loggerApplicationID);
    private final String newContextAttachedConfirmationMessage = String.format("New logging context information attached to logger! Log level: '%s' | Appender type: '%s' (INTERNAL / IGNORE)",LoggingContextInformation.errorLogLevel,LoggingContextInformation.consoleAppender);
    private final String shiftingToAttachedContextInitializationMessage = String.format("Shifting to attached context information! Log level: '%s' | Appender type: '%s' (INTERNAL / IGNORE)",LoggingContextInformation.errorLogLevel,LoggingContextInformation.consoleAppender);
    private final String shiftingToAttachedContextConfirmationMessage = String.format("Shifted to attached logging context for application ID: '%s' | Log level: '%s' | Appender type: '%s' (INTERNAL / IGNORE)'",loggerApplicationID,LoggingContextInformation.errorLogLevel,LoggingContextInformation.consoleAppender);


    @BeforeEach
    void setUp() {
        try {
            this.consoleOut = Files.createTempFile("jftf_shift_test_stdout", ".txt").toFile();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        try {
            System.setOut(new PrintStream(this.consoleOut));
            this.consoleOutIs = new FileInputStream(this.consoleOut);
        }
        catch(FileNotFoundException e){
            System.err.println("Failed to redirect console out into temp file!");
            e.printStackTrace();
        }
        this.Logger = LoggingController.LoggerFactory(new LoggingContextInformation(loggerApplicationID,LoggingContextInformation.defaultLogLevel,LoggingContextInformation.defaultAppender),true);
    }

    private String readConsoleOut(){
        try{
            StringBuilder consoleOutBuilder = new StringBuilder();
            try (Reader reader = new BufferedReader(new InputStreamReader
                    (this.consoleOutIs, Charset.forName(StandardCharsets.UTF_8.name())))) {
                int c;
                while ((c = reader.read()) != -1) {
                    consoleOutBuilder.append((char) c);
                }
            }
            return  consoleOutBuilder.toString();
        }
        catch (IOException e){
            System.err.println("Failed to read from console out temp file!");
            e.printStackTrace();
        }
        return null;
    }

    @Test
    @DisplayName("Shifting to new context information should produce the correct shifting confirmation messages for both appender and log level. This also tests the internal shifting of log levels")
    void LoggingControllerShiftToNewContextInformation() {
        this.Logger.shiftToNewContextInformation(new LoggingContextInformation(LoggingContextInformation.errorLogLevel,LoggingContextInformation.consoleAppender));
        this.Logger.LogError(errorLogLevelCheckMessage);
        this.Logger.LogInfo(infoLogLevelCheckMessage);
        String consoleOutContent = readConsoleOut();
        try {
            assertTrue(Objects.requireNonNull(consoleOutContent).contains(newContextShiftingInitializationMessage));
            assertTrue(Objects.requireNonNull(consoleOutContent).contains(newContextShiftingConfirmationMessage));
            assertTrue(Objects.requireNonNull(consoleOutContent).contains(newLogLevelShiftingInitializationMessage));
            assertTrue(Objects.requireNonNull(consoleOutContent).contains(newLogLevelShiftingConfirmationMessage));
            assertTrue(Objects.requireNonNull(consoleOutContent).contains(expectedErrorLogLevelCheckMessage));
            assertFalse(Objects.requireNonNull(consoleOutContent).contains(expectedInfoLogLevelCheckMessage));
        }
        catch (AssertionError e){
            System.err.println("RECEIVED");
            System.err.println(consoleOutContent);
            System.err.println("EXPECTED");
            System.err.println(newContextShiftingInitializationMessage);
            System.err.println(newContextShiftingConfirmationMessage);
            System.err.println(newLogLevelShiftingInitializationMessage);
            System.err.println(newLogLevelShiftingConfirmationMessage);
            System.err.println(expectedErrorLogLevelCheckMessage);
            System.err.println(expectedInfoLogLevelCheckMessage);
            throw e;
        }
    }

    @Test
    @DisplayName("Attaching a new logging context to a logger, then shifting to the attached logging context should shift the configuration to the newly attached one.")
    void LoggingControllerAttachNewContextInformation(){
        this.Logger.attachNewContextInformation(new LoggingContextInformation(LoggingContextInformation.errorLogLevel,LoggingContextInformation.consoleAppender));
        this.Logger.shiftToAttachedContextInformation();
        this.Logger.LogError(errorLogLevelCheckMessage);
        this.Logger.LogInfo(infoLogLevelCheckMessage);
        String consoleOutContent = readConsoleOut();
        try {
            assertTrue(Objects.requireNonNull(consoleOutContent).contains(newContextAttachedConfirmationMessage));
            assertTrue(Objects.requireNonNull(consoleOutContent).contains(shiftingToAttachedContextInitializationMessage));
            assertTrue(Objects.requireNonNull(consoleOutContent).contains(shiftingToAttachedContextConfirmationMessage));
            assertTrue(Objects.requireNonNull(consoleOutContent).contains(newLogLevelShiftingInitializationMessage));
            assertTrue(Objects.requireNonNull(consoleOutContent).contains(newLogLevelShiftingConfirmationMessage));
            assertTrue(Objects.requireNonNull(consoleOutContent).contains(expectedErrorLogLevelCheckMessage));
            assertFalse(Objects.requireNonNull(consoleOutContent).contains(expectedInfoLogLevelCheckMessage));
        }
        catch (AssertionError e){
            System.err.println("RECEIVED");
            System.err.println(consoleOutContent);
            System.err.println("EXPECTED");
            System.err.println(newContextAttachedConfirmationMessage);
            System.err.println(shiftingToAttachedContextInitializationMessage);
            System.err.println(shiftingToAttachedContextConfirmationMessage);
            System.err.println(newLogLevelShiftingInitializationMessage);
            System.err.println(newLogLevelShiftingConfirmationMessage);
            System.err.println(expectedErrorLogLevelCheckMessage);
            System.err.println(expectedInfoLogLevelCheckMessage);
            throw e;
        }
    }

    @Test
    @DisplayName("Only debug level messages should be visible")
    void LoggingControllerDebugLogLevelTest() {
        this.Logger.shiftToNewContextInformation(new LoggingContextInformation(LoggingContextInformation.debugLogLevel,LoggingContextInformation.consoleAppender));
        this.Logger.LogDebug(debugLogLevelCheckMessage);
        this.Logger.LogInfo(infoLogLevelCheckMessage);
        this.Logger.LogError(errorLogLevelCheckMessage);
        String consoleOutContent = readConsoleOut();
        try {
            assertTrue(Objects.requireNonNull(consoleOutContent).contains(expectedDebugLogLevelCheckMessage));
            assertTrue(Objects.requireNonNull(consoleOutContent).contains(expectedInfoLogLevelCheckMessage));
            assertTrue(Objects.requireNonNull(consoleOutContent).contains(expectedErrorLogLevelCheckMessage));
        }
        catch (AssertionError e){
            System.err.println("RECEIVED");
            System.err.println(consoleOutContent);
            System.err.println("EXPECTED");
            System.err.println(expectedDebugLogLevelCheckMessage);
            System.err.println(expectedInfoLogLevelCheckMessage);
            System.err.println(expectedErrorLogLevelCheckMessage);
            throw e;
        }
    }

    @Test
    @DisplayName("Only info level messages should be visible")
    void LoggingControllerInfoLogLevelTest() {
        this.Logger.shiftToNewContextInformation(new LoggingContextInformation(LoggingContextInformation.infoLogLevel,LoggingContextInformation.consoleAppender));
        this.Logger.LogDebug(debugLogLevelCheckMessage);
        this.Logger.LogInfo(infoLogLevelCheckMessage);
        this.Logger.LogError(errorLogLevelCheckMessage);
        String consoleOutContent = readConsoleOut();
        try {
            assertFalse(Objects.requireNonNull(consoleOutContent).contains(expectedDebugLogLevelCheckMessage));
            assertTrue(Objects.requireNonNull(consoleOutContent).contains(expectedInfoLogLevelCheckMessage));
            assertTrue(Objects.requireNonNull(consoleOutContent).contains(expectedErrorLogLevelCheckMessage));
        }
        catch (AssertionError e){
            System.err.println("RECEIVED");
            System.err.println(consoleOutContent);
            System.err.println("EXPECTED");
            System.err.println(expectedInfoLogLevelCheckMessage);
            System.err.println(expectedErrorLogLevelCheckMessage);
            throw e;
        }
    }

    @Test
    @DisplayName("Only error level messages should be visible")
    void LoggingControllerErrorLogLevelTest() {
        this.Logger.shiftToNewContextInformation(new LoggingContextInformation(LoggingContextInformation.errorLogLevel,LoggingContextInformation.consoleAppender));
        this.Logger.LogDebug(debugLogLevelCheckMessage);
        this.Logger.LogInfo(infoLogLevelCheckMessage);
        this.Logger.LogError(errorLogLevelCheckMessage);
        String consoleOutContent = readConsoleOut();
        try {
            assertFalse(Objects.requireNonNull(consoleOutContent).contains(expectedDebugLogLevelCheckMessage));
            assertFalse(Objects.requireNonNull(consoleOutContent).contains(expectedInfoLogLevelCheckMessage));
            assertTrue(Objects.requireNonNull(consoleOutContent).contains(expectedErrorLogLevelCheckMessage));
        }
        catch (AssertionError e){
            System.err.println("RECEIVED");
            System.err.println(consoleOutContent);
            System.err.println("EXPECTED");
            System.err.println(expectedErrorLogLevelCheckMessage);
            throw e;
        }
    }

    @Test
    @DisplayName("Logging to minimum log level should automatically be done on the lowest possible log level for the current context configuration")
    void LoggingControllerLogToMinimumLogLevelTest(){
        this.Logger.shiftToNewContextInformation(new LoggingContextInformation(LoggingContextInformation.errorLogLevel,LoggingContextInformation.consoleAppender));
        this.Logger.LogToMinimumLogLevel(errorLogLevelCheckMessage);
        this.Logger.shiftToNewContextInformation(new LoggingContextInformation(LoggingContextInformation.infoLogLevel,LoggingContextInformation.consoleAppender));
        this.Logger.LogToMinimumLogLevel(infoLogLevelCheckMessage);
        this.Logger.shiftToNewContextInformation(new LoggingContextInformation(LoggingContextInformation.debugLogLevel,LoggingContextInformation.consoleAppender));
        this.Logger.LogToMinimumLogLevel(debugLogLevelCheckMessage);
        String consoleOutContent = readConsoleOut();
        try {
            assertTrue(Objects.requireNonNull(consoleOutContent).contains(expectedDebugLogLevelCheckMessage));
            assertTrue(Objects.requireNonNull(consoleOutContent).contains(expectedInfoLogLevelCheckMessage));
            assertTrue(Objects.requireNonNull(consoleOutContent).contains(expectedErrorLogLevelCheckMessage));
        }
        catch (AssertionError e){
            System.err.println("RECEIVED");
            System.err.println(consoleOutContent);
            System.err.println("EXPECTED");
            System.err.println(expectedDebugLogLevelCheckMessage);
            System.err.println(expectedInfoLogLevelCheckMessage);
            System.err.println(expectedErrorLogLevelCheckMessage);
            throw e;
        }
    }

    @AfterAll
    void tearDown() {
        if(!this.consoleOut.delete())
            System.err.println("Failed to delete temp file!");
    }
}