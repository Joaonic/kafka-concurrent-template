package br.gft.template.demo.exceptions;

public class UnauthenticatedException extends BaseException {

    public static final String KEY = "exceptions.rest.unauthenticated";

    public UnauthenticatedException() {
        super(KEY);
    }

    public UnauthenticatedException(String message) {
        super(KEY, message);
    }

    public UnauthenticatedException(Throwable cause) {
        super(KEY, cause);
    }

    public UnauthenticatedException(String message, Throwable cause) {
        super(KEY, message, cause);
    }

    public UnauthenticatedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(KEY, message, cause, enableSuppression, writableStackTrace);
    }

}
