package br.gft.template.demo.exceptions;

public class ConflictException extends BaseException {

    public static final String KEY = "exceptions.rest.conflict";

    public ConflictException() {
        super(KEY);
    }

    public ConflictException(String message) {
        super(KEY, message);
    }

    public ConflictException(Throwable cause) {
        super(KEY, cause);
    }

    public ConflictException(String message, Throwable cause) {
        super(KEY, message, cause);
    }

    public ConflictException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(KEY, message, cause, enableSuppression, writableStackTrace);
    }

}
