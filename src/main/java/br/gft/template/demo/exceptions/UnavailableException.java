package br.gft.template.demo.exceptions;

public class UnavailableException extends BaseException {

    public static final String KEY = "exceptions.rest.unavailable";

    public UnavailableException() {
        super(KEY);
    }

    public UnavailableException(String message) {
        super(KEY, message);
    }

    public UnavailableException(Throwable cause) {
        super(KEY, cause);
    }

    public UnavailableException(String message, Throwable cause) {
        super(KEY, message, cause);
    }

    public UnavailableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(KEY, message, cause, enableSuppression, writableStackTrace);
    }

}
