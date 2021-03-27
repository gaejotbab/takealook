package dev.gaejotbab.takealook;

public class HttpRequestParsingFailedException extends TakealookException {
    public HttpRequestParsingFailedException() {
    }

    public HttpRequestParsingFailedException(String message) {
        super(message);
    }

    public HttpRequestParsingFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpRequestParsingFailedException(Throwable cause) {
        super(cause);
    }

    public HttpRequestParsingFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
