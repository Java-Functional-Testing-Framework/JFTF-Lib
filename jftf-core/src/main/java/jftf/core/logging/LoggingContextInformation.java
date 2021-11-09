package jftf.core.logging;

public class LoggingContextInformation {
    private final String currentApplicationID;
    public LoggingContextInformation(String currentApplicationID) {
        this.currentApplicationID = currentApplicationID;
    }

    public String getCurrentApplicationID() {
        return currentApplicationID;
    }
}
