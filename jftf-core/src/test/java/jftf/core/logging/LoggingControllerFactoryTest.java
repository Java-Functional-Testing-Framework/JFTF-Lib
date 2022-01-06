package jftf.core.logging;

import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class LoggingControllerFactoryTest {
    private LoggingController Logger;
    private File consoleOut = null;
    private InputStream consoleOutIs;
    private final String loggerApplicationID = "LoggerFactoryTest";
    private final String defaultContextInitializationMessage = String.format("Shifting to attached context information! Log level: '%s' | Appender type: '%s' (INTERNAL / IGNORE)",LoggingContextInformation.defaultLogLevel.levelStr,LoggingContextInformation.defaultAppender);
    private final String defaultLogShiftConfirmationMessage = String.format("Shifted to attached logging context for application ID: '%s' | Log level: '%s' | Appender type: '%s' (INTERNAL / IGNORE)",loggerApplicationID,LoggingContextInformation.defaultLogLevel.levelStr,LoggingContextInformation.defaultAppender);
    private final String defaultLogLevelValidationMessage = String.format("%s  %s - Log level validation message!",LoggingContextInformation.defaultLogLevel.levelStr,loggerApplicationID);
    @BeforeEach
    void setUp() {
        try {
            this.consoleOut = Files.createTempFile("jftf_test_stdout", ".txt").toFile();
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
        this.Logger = LoggingController.LoggerFactory(new LoggingContextInformation(loggerApplicationID,LoggingContextInformation.defaultLogLevel,LoggingContextInformation.defaultAppender),true,true);
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
    @DisplayName("LoggerFactory should return a configured logger with the default log level, appender and application ID as given in the factory method")
    public void LoggingControllerFactoryConfigurationTest() {
        String consoleOutContent = readConsoleOut();
        try {
            assertTrue(Objects.requireNonNull(consoleOutContent).contains(loggerApplicationID));
            assertTrue(Objects.requireNonNull(consoleOutContent).contains(defaultContextInitializationMessage));
            assertTrue(Objects.requireNonNull(consoleOutContent).contains(defaultLogShiftConfirmationMessage));
        }
        catch (AssertionError e){
            System.err.println("RECEIVED");
            System.err.println(consoleOutContent);
            System.err.println("EXPECTED");
            System.err.println(loggerApplicationID);
            System.err.println(defaultContextInitializationMessage);
            System.err.println(defaultLogShiftConfirmationMessage);
            throw e;
        }
    }

    @Test
    @DisplayName("Lowest log level should correspond with the default log level given to LoggerFactory, if configuration for the logger context worked as expected")
    public void LoggingControllerFactoryLogLevelTest(){
        this.Logger.LogToMinimumLogLevel("Log level validation message!");
        String consoleOutContent = readConsoleOut();
        try {
            assertTrue(Objects.requireNonNull(consoleOutContent).contains(defaultLogLevelValidationMessage));
        }
        catch (AssertionError e){
            System.err.println("RECEIVED");
            System.err.println(consoleOutContent);
            System.err.println("EXPECTED");
            System.err.println(defaultLogLevelValidationMessage);
            throw e;
        }
    }

    @AfterEach
    void Teardown(){
        if(!this.consoleOut.delete())
            System.err.println("Failed to delete temp file!");
    }
}