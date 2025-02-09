package br.gft.template.demo.exceptions;

public class InternalException extends BaseException {

    public static final String KEY = "exceptions.rest.internal";

    public InternalException() {
        super(KEY);
    }

    public InternalException(String message) {
        super(KEY, message);
    }

    public InternalException(Throwable cause) {
        super(KEY, cause);
    }

    public InternalException(String message, Throwable cause) {
        super(KEY, message, cause);
    }

    public InternalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(KEY, message, cause, enableSuppression, writableStackTrace);
    }

}
