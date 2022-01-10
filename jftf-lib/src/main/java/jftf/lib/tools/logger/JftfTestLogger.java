package jftf.lib.tools.logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

public final class JftfTestLogger implements IJftfTestLogger{
    private static JftfTestLogger jftfTestLoggerInstance = null;
    private List<String> outputMessageList;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.S");
    private final String loggerName;

    private JftfTestLogger(String loggerName){
        this.outputMessageList = new ArrayList<>();
        this.loggerName = loggerName;
    }

    public static JftfTestLogger JftfTestLoggerFactory(String loggerName){
        if(jftfTestLoggerInstance == null){
            jftfTestLoggerInstance = new JftfTestLogger(loggerName);
        }
        return jftfTestLoggerInstance;
    }

    public static JftfTestLogger getJftfTestLogger(){
        return jftfTestLoggerInstance;
    }

    @Override
    public void LogInfo(String logMessage) {
        logMessage = this.formatLogMessage(logMessage,Level.INFO);
        System.out.println(logMessage);
        this.outputMessageList.add(logMessage);
    }

    @Override
    public void LogWarning(String logMessage) {
        logMessage = this.formatLogMessage(logMessage,Level.WARNING);
        System.out.println(logMessage);
        this.outputMessageList.add(logMessage);
    }

    @Override
    public void LogError(String logMessage) {
        logMessage = this.formatLogMessage(logMessage,Level.SEVERE);
        System.out.println(logMessage);
        this.outputMessageList.add(logMessage);
    }

    @Override
    public List<String> getOutputMessagesList() {
        return this.outputMessageList;
    }

    @Override
    public String getOutputMessage() {
        StringBuilder outputMessage = new StringBuilder();
        for (String message : this.outputMessageList){
            outputMessage.append(message+"\n");
        }
        return outputMessage.toString();
    }

    @Override
    public void setDateFormat(SimpleDateFormat simpleDateFormat) {
        this.simpleDateFormat = simpleDateFormat;
    }

    private String formatLogMessage(String logMessage, Level logLevel){
        return String.format("%s --- %s --- %s: %s",this.simpleDateFormat.format(new Date()),this.loggerName,logLevel.toString(),logMessage);
    }
}
