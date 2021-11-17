package jftf.core.logging;

import ch.qos.logback.classic.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class LoggingContextInformation {
    public final static Level debugLogLevel = Level.DEBUG;
    public final static Level infoLogLevel = Level.INFO;
    public final static Level errorLogLevel = Level.ERROR;
    public final static Level defaultLogLevel = infoLogLevel;
    public final static String consoleAppender = "consoleAppender";
    private final static String consoleAppenderConfiguration = "logback.xml";
    public final static String fileAppender = "fileAppender";
    private final static String fileAppenderConfiguration = "logback_file.xml";
    public final static String defaultAppender = consoleAppender;
    private final static String defaultAppenderConfiguration = consoleAppenderConfiguration;
    public final static ArrayList<Level> validLogLevels = new ArrayList<>(Arrays.asList(debugLogLevel,infoLogLevel,errorLogLevel));
    public final static ArrayList<String> validAppenders = new ArrayList<>(Arrays.asList(consoleAppender,fileAppender));
    private String currentApplicationID = "";
    private Level baseLogLevel = defaultLogLevel;
    private String baseAppender = defaultAppender;

    public LoggingContextInformation(String currentApplicationID, Level baseLogLevel, String baseAppender) {
        this.currentApplicationID = currentApplicationID;
        if(validLogLevels.contains(baseLogLevel))
            this.baseLogLevel = baseLogLevel;
        if(validAppenders.contains(baseAppender)){
            this.baseAppender = baseAppender;
        }
    }

    public LoggingContextInformation(Level baseLogLevel, String baseAppender) {
        if(validLogLevels.contains(baseLogLevel))
            this.baseLogLevel = baseLogLevel;
        if(validAppenders.contains(baseAppender)){
            this.baseAppender = baseAppender;
        }
    }

    public String getApplicationID() {
        return this.currentApplicationID;
    }

    public void setApplicationID(String currentApplicationID){
        this.currentApplicationID = currentApplicationID;
    }

    public Level getLogLevel(){
        return this.baseLogLevel;
    }

    public String getAppender(){
        return this.baseAppender;
    }

    public String getAppenderConfiguration(){
        if(Objects.equals(this.baseAppender, LoggingContextInformation.consoleAppender))
            return LoggingContextInformation.consoleAppenderConfiguration;
        else if (Objects.equals(this.baseAppender, LoggingContextInformation.fileAppender))
            return LoggingContextInformation.fileAppenderConfiguration;
        else
            return LoggingContextInformation.defaultAppenderConfiguration;
    }

}
