package br.gft.template.demo.exceptions;

public class NotFoundException extends BaseException {

    public static final String KEY = "exceptions.rest.not_found";

    public NotFoundException() {
        super(KEY);
    }

    public NotFoundException(String message) {
        super(KEY, message);
    }

    public NotFoundException(Throwable cause) {
        super(KEY, cause);
    }

    public NotFoundException(String message, Throwable cause) {
        super(KEY, message, cause);
    }

    public NotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(KEY, message, cause, enableSuppression, writableStackTrace);
    }

}
