package br.gft.template.demo.exceptions;

public class TimeoutException extends RetryableException {

    public static final String KEY = "exceptions.rest.timeout";

    public TimeoutException() {
        super(KEY);
    }

    public TimeoutException(String message) {
        super(KEY, message);
    }

    public TimeoutException(Throwable cause) {
        super(KEY, cause);
    }

    public TimeoutException(String message, Throwable cause) {
        super(KEY, message, cause);
    }

    public TimeoutException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(KEY, message, cause, enableSuppression, writableStackTrace);
    }

}
