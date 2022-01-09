package jftf.lib.tools.assertions;

public class AssertionFailedError extends AssertionError{


    public AssertionFailedError() {}

    public AssertionFailedError(String message){
        super(defaultString(message));
    }

    private static String defaultString(String message) {
        return message == null ? "" : message;
    }
}
