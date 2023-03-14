package jftf.core.logging;

public enum ExitCode {
    AJPA_ORM_ERROR(5);

    private final int code;

    ExitCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}