package br.gft.template.demo.exceptions;

public class InvalidArgumentException extends BaseException {

    public static final String KEY = "exceptions.rest.invalid_argument";

    public InvalidArgumentException() {
        super(KEY);
    }

    public InvalidArgumentException(String message) {
        super(KEY, message);
    }

    public InvalidArgumentException(Throwable cause) {
        super(KEY, cause);
    }

    public InvalidArgumentException(String message, Throwable cause) {
        super(KEY, message, cause);
    }

    public InvalidArgumentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(KEY, message, cause, enableSuppression, writableStackTrace);
    }

}
