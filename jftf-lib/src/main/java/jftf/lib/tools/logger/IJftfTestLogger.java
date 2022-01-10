package jftf.lib.tools.logger;

import java.text.SimpleDateFormat;
import java.util.List;

public interface IJftfTestLogger {
    public void LogInfo(String logMessage);
    public void LogWarning(String logMessage);
    public void LogError(String logMessage);
    public List<String> getOutputMessagesList();
    public String getOutputMessage();
    public void setDateFormat(SimpleDateFormat simpleDateFormat);
}
