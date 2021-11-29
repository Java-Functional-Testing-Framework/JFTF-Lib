package jftf.core.logging;

import org.junit.jupiter.api.*;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
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
    private final String shiftingToFileAppenderInitializationMessage = String.format("Shifting to new logging context information! Log level: '%s' | Appender type: '%s' (INTERNAL / IGNORE)",LoggingContextInformation.infoLogLevel,LoggingContextInformation.fileAppender);
    private final String shiftingToFileAppenderConfirmationMessage = String.format("Shifted to new logging context for application ID: '%s' | Log level: '%s' | Appender type: '%s' (INTERNAL / IGNORE)",loggerApplicationID,LoggingContextInformation.infoLogLevel,LoggingContextInformation.fileAppender);
    private final Path jftfHomePath = Paths.get(System.getProperty("user.home"),".jftf");
    private final Path jftfHomePathLog = Paths.get(jftfHomePath.toString(),"logs");
    private final Path jftfHomePathLogCurrentApp = Paths.get(jftfHomePathLog.toString(),loggerApplicationID);
    private final String faultModeLoggerReason = "JFTF fault mode logger test!";
    private final String faultModeLoggerConfirmationMessage = String.format("[SEVERE ] Logging controller switching to Java fault mode logger! Reason --> java.lang.Exception: %s",faultModeLoggerReason);
    private final String faultModeLoggerFileHandlerRegistrationConfirmationMessage = "[INFO   ] Registered new file handler for Java fault logger with file path:";
    private final String javaLoggerExpectedDebugLogLevelCheckMessage = String.format("[FINEST ] %s",debugLogLevelCheckMessage);
    private final String javaLoggerExpectedInfoLogLevelCheckMessage = String.format("[INFO   ] %s",infoLogLevelCheckMessage);
    private final String javaLoggerExpectedErrorLogLevelCheckMessage = String.format("[SEVERE ] %s",errorLogLevelCheckMessage);
    private final String javaLoggerMinimumLogMessage = "Minimum log message!";
    private final String javaLoggerExpectedMinimumLogLevelCheckMessage = String.format("[FINEST ] %s",javaLoggerMinimumLogMessage);
    private final String syslogServerIp = "localhost";
    private final String syslogLogFileUri = "/var/log/syslog";
    private final String shiftingToSyslogAppenderInitializationMessage = String.format("Shifting to new logging context information! Log level: '%s' | Appender type: '%s' (INTERNAL / IGNORE)",LoggingContextInformation.infoLogLevel,LoggingContextInformation.syslogAppender);
    private final String shiftingToSyslogAppenderConfirmationMessage = String.format("Shifted to new logging context for application ID: '%s' | Log level: '%s' | Appender type: '%s' (INTERNAL / IGNORE)",loggerApplicationID,LoggingContextInformation.infoLogLevel,LoggingContextInformation.syslogAppender);

    @BeforeAll
    void initScenario(){
        if(!Files.exists(jftfHomePath)){
            try {
                Files.createDirectory(jftfHomePath);
            }
            catch(IOException e){
                System.err.println("Couldn't create .jftf home directory!");
                e.printStackTrace();
            }
        }
        System.setProperty("JFTF_LOGS",jftfHomePathLog.toString());
        System.setProperty("CUR_APP_NAME",loggerApplicationID);
        System.setProperty("SYSLOG_SERVER_IP",syslogServerIp);
    }

    @BeforeEach
    void setUp() {
        try {
            this.consoleOut = Files.createTempFile("jftf_shift_test_stdout", ".txt").toFile();
        }
        catch(IOException e){
            System.err.println("Failed to create stdout temp file!");
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

    private String readFromInputStream(InputStream inputStream){
        try{
            StringBuilder fileStringBuilder = new StringBuilder();
            try (Reader reader = new BufferedReader(new InputStreamReader
                    (inputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
                int c;
                while ((c = reader.read()) != -1) {
                    fileStringBuilder.append((char) c);
                }
            }
            return  fileStringBuilder.toString();
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

    @Test
    @DisplayName("Shifting to file appender should produce a log file inside the jftf home directory, and all logging should be directed into this file")
    void LoggingControllerShiftToFileAppender(){
        this.Logger.shiftToNewContextInformation(new LoggingContextInformation(LoggingContextInformation.infoLogLevel,LoggingContextInformation.fileAppender));
        this.Logger.LogToMinimumLogLevel(infoLogLevelCheckMessage);
        assertTrue(Files.exists(jftfHomePathLogCurrentApp));
        File currentAppLogDirectory = jftfHomePathLogCurrentApp.toFile();
        assertEquals(Arrays.stream(Objects.requireNonNull(currentAppLogDirectory.listFiles())).count(),1);
        try {
            InputStream logFileInputStream = new FileInputStream(Objects.requireNonNull(currentAppLogDirectory.listFiles())[0]);
            String logFileContent = readFromInputStream(logFileInputStream);
            String consoleOutContent = readConsoleOut();
            try{
                assertTrue(Objects.requireNonNull(consoleOutContent).contains(shiftingToFileAppenderInitializationMessage));
                assertTrue(Objects.requireNonNull(logFileContent).contains(shiftingToFileAppenderConfirmationMessage));
                assertTrue(Objects.requireNonNull(logFileContent).contains(expectedInfoLogLevelCheckMessage));
            }
            catch (AssertionError e){
                System.err.println("RECEIVED");
                System.err.println(consoleOutContent);
                System.err.println(logFileContent);
                System.err.println("EXPECTED");
                System.err.println(shiftingToFileAppenderInitializationMessage);
                System.err.println(shiftingToFileAppenderConfirmationMessage);
                System.err.println(expectedInfoLogLevelCheckMessage);
                throw e;
            }
        }
        catch (FileNotFoundException e){
            System.err.println("Log file not found!");
            e.printStackTrace();
        }
        try {
            Arrays.stream(Objects.requireNonNull(currentAppLogDirectory.listFiles())).forEach(File::delete);
            assertTrue(currentAppLogDirectory.delete());
        }
        catch(Exception e){
            System.err.println("Failed to delete test log directory!");
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Java fault mode logging should get triggered whenever the logback framework fails to initialize correctly. The java logger should register a file and console handler, and all logs should be directed to these handlers")
    void LoggingControllerFaultModeLoggerTest(){
        this.Logger.switchToFaultMode(new Exception(faultModeLoggerReason));
        this.Logger.LogDebug(debugLogLevelCheckMessage);
        this.Logger.LogInfo(infoLogLevelCheckMessage);
        this.Logger.LogError(errorLogLevelCheckMessage);
        this.Logger.LogToMinimumLogLevel(javaLoggerMinimumLogMessage);
        String consoleOutContent = readConsoleOut();
        Path logFilePath = Path.of(consoleOutContent.substring(consoleOutContent.indexOf("'") + 1, consoleOutContent.indexOf("'",consoleOutContent.indexOf("'")+1)));
        assertTrue(Files.exists(logFilePath));
        File logFile = logFilePath.toFile();
        try {
            InputStream logFileInputStream = new FileInputStream(logFile);
            String logFileContent = readFromInputStream(logFileInputStream);
            try {
                assertTrue(Objects.requireNonNull(consoleOutContent).contains(faultModeLoggerConfirmationMessage));
                assertTrue(Objects.requireNonNull(consoleOutContent).contains(faultModeLoggerFileHandlerRegistrationConfirmationMessage));
                assertTrue(Objects.requireNonNull(logFileContent).contains(faultModeLoggerConfirmationMessage));
                assertTrue(Objects.requireNonNull(logFileContent).contains(faultModeLoggerFileHandlerRegistrationConfirmationMessage));
                assertTrue(Objects.requireNonNull(logFileContent).contains(javaLoggerExpectedDebugLogLevelCheckMessage));
                assertTrue(Objects.requireNonNull(logFileContent).contains(javaLoggerExpectedInfoLogLevelCheckMessage));
                assertTrue(Objects.requireNonNull(logFileContent).contains(javaLoggerExpectedErrorLogLevelCheckMessage));
                assertTrue(Objects.requireNonNull(logFileContent).contains(javaLoggerExpectedMinimumLogLevelCheckMessage));
            } catch (AssertionError e) {
                System.err.println("RECEIVED");
                System.err.println(consoleOutContent);
                System.err.println("EXPECTED");
                System.err.println(faultModeLoggerFileHandlerRegistrationConfirmationMessage);
                System.err.println(faultModeLoggerConfirmationMessage);
                System.err.println(javaLoggerExpectedDebugLogLevelCheckMessage);
                System.err.println(javaLoggerExpectedInfoLogLevelCheckMessage);
                System.err.println(javaLoggerExpectedErrorLogLevelCheckMessage);
                System.err.println(javaLoggerExpectedMinimumLogLevelCheckMessage);
                throw e;
            }
        }
        catch (FileNotFoundException e){
            System.err.println("Log file not found!");
            e.printStackTrace();
        }
        this.Logger.switchToLogbackMode();
        try{
            assertTrue(logFile.delete());
        }
        catch (Exception e){
            System.err.println("Failed to delete fault logger log file!");
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Shifting to logback syslog appender should log via the syslog network protocol to the system syslog log file. This tests requires a syslog daemon configured to accept network logs")
    void LoggingControllerShiftToSyslogAppender(){
        this.Logger.shiftToNewContextInformation(new LoggingContextInformation(LoggingContextInformation.defaultLogLevel,LoggingContextInformation.defaultAppender));
        Path syslogLogFilePath = Path.of(syslogLogFileUri);
        assertTrue(Files.exists(syslogLogFilePath));
        File syslogLogFile = syslogLogFilePath.toFile();
        try {
            InputStream logFileInputStream = new FileInputStream(syslogLogFile);
            String syslogLogFileContentBefore = readFromInputStream(logFileInputStream);
            this.Logger.shiftToNewContextInformation(new LoggingContextInformation(LoggingContextInformation.defaultLogLevel,LoggingContextInformation.syslogAppender));
            this.Logger.LogToMinimumLogLevel(infoLogLevelCheckMessage);
            logFileInputStream = new FileInputStream(syslogLogFile);
            String syslogLogFileContent = readFromInputStream(logFileInputStream);
            String consoleOutContent = readConsoleOut();
            String syslogLogDifference = StringUtils.difference(syslogLogFileContentBefore,syslogLogFileContent);
            try {
                assertTrue(Objects.requireNonNull(consoleOutContent).contains(shiftingToSyslogAppenderInitializationMessage));
                assertTrue(Objects.requireNonNull(syslogLogDifference).contains(shiftingToSyslogAppenderConfirmationMessage));
                assertTrue(Objects.requireNonNull(syslogLogDifference).contains(expectedInfoLogLevelCheckMessage));
            } catch (AssertionError e) {
                System.err.println("RECEIVED");
                System.err.println(consoleOutContent);
                System.err.println("EXPECTED");
                System.err.println(shiftingToSyslogAppenderInitializationMessage);
                System.err.println(shiftingToSyslogAppenderConfirmationMessage);
                System.err.println(expectedInfoLogLevelCheckMessage);
                throw e;
            }
        }
        catch (FileNotFoundException e){
            System.err.println("Syslog log file not found!");
            e.printStackTrace();
        }

    }

    @AfterEach
    void tearDown(){
        if(!this.consoleOut.delete())
            System.err.println("Failed to delete temp file!");
    }
}