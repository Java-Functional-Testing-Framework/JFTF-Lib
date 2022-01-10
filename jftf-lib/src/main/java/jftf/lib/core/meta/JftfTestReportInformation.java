package jftf.lib.core.meta;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

public class JftfTestReportInformation {
    private int testId;
    private Timestamp startupTimestamp;
    private Timestamp endTimestamp = null;
    private Time testDuration = null;
    private String errorMessages = "";
    private String loggerOutput = "";
    private String executionResult = "";
    public static final String successfulState = "successfulState";
    public static final String errorState = "errorState";

    @Override
    public String toString() {
        return "JftfTestReportInformation{" +
                "testId=" + testId +
                ", startupTimestamp=" + startupTimestamp +
                ", endTimestamp=" + endTimestamp +
                ", testDuration=" + testDuration +
                ", errorMessages='" + errorMessages + '\'' +
                ", loggerOutput='" + loggerOutput + '\'' +
                ", executionResult='" + executionResult + '\'' +
                '}';
    }

    public JftfTestReportInformation(Timestamp startupTimestamp){
        this.testId = testId;
        this.startupTimestamp = startupTimestamp;
    }

    public int getTestId() {
        return this.testId;
    }

    public void setTestId(int testId) {
        this.testId = testId;
    }

    public Timestamp getStartupTimestamp() {
        return this.startupTimestamp;
    }

    public void setStartupTimestamp(Timestamp startupTimestamp) {
        this.startupTimestamp = startupTimestamp;
    }

    public Timestamp getEndTimestamp() {
        return this.endTimestamp;
    }

    public void setEndTimestamp(Timestamp endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public Time getTestDuration() {
        return this.testDuration;
    }

    public void setTestDuration(Time testDuration) {
        this.testDuration = testDuration;
    }

    public String getErrorMessages() {
        return this.errorMessages;
    }

    public void setErrorMessages(List<String> errorMessages) {
        StringBuilder errorMessagesBuilder = new StringBuilder();
        for (String errorMessage : errorMessages){
            errorMessagesBuilder.append(errorMessage);
        }
        this.errorMessages = String.valueOf(errorMessagesBuilder);
    }

    public String getLoggerOutput() {
        return this.loggerOutput;
    }

    public void setLoggerOutput(String loggerOutput) {
        this.loggerOutput = loggerOutput;
    }

    public String getExecutionResult() {
        return this.executionResult;
    }

    public void setExecutionResult(String executionResult) {
        if(Objects.equals(executionResult, successfulState) || Objects.equals(executionResult, errorState)) {
            this.executionResult = executionResult;
        }
    }
}
