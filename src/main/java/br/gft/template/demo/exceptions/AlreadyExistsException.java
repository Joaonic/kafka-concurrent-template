package br.gft.template.demo.exceptions;

public class AlreadyExistsException extends BaseException {

    public static final String KEY = "exceptions.rest.already_exists";

    public AlreadyExistsException() {
        super(KEY);
    }

    public AlreadyExistsException(String message) {
        super(KEY, message);
    }

    public AlreadyExistsException(Throwable cause) {
        super(KEY, cause);
    }

    public AlreadyExistsException(String message, Throwable cause) {
        super(KEY, message, cause);
    }

    public AlreadyExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(KEY, message, cause, enableSuppression, writableStackTrace);
    }

}
