package br.gft.template.demo.exceptions;

public class TooManyRequestsException extends BaseException {

    public static final String KEY = "exceptions.rest.too_many_requests";

    public TooManyRequestsException() {
        super(KEY);
    }

    public TooManyRequestsException(String message) {
        super(KEY, message);
    }

    public TooManyRequestsException(Throwable cause) {
        super(KEY, cause);
    }

    public TooManyRequestsException(String message, Throwable cause) {
        super(KEY, message, cause);
    }

    public TooManyRequestsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(KEY, message, cause, enableSuppression, writableStackTrace);
    }

}
