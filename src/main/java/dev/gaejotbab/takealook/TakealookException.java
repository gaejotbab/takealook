package dev.gaejotbab.takealook;

public class TakealookException extends RuntimeException {
    public TakealookException() {
    }

    public TakealookException(String message) {
        super(message);
    }

    public TakealookException(String message, Throwable cause) {
        super(message, cause);
    }

    public TakealookException(Throwable cause) {
        super(cause);
    }

    public TakealookException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
