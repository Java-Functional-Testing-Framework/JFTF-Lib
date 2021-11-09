package jftf.core.logging;

public class LoggerFailedInitialization extends RuntimeException{
    public LoggerFailedInitialization(String message, Throwable cause) {
        super(message, cause);
    }
}
