package br.gft.template.demo.exceptions;

public class NotImplementedException extends BaseException {

    public static final String KEY = "exceptions.rest.not_implemented";

    public NotImplementedException() {
        super(KEY);
    }

    public NotImplementedException(String message) {
        super(KEY, message);
    }

    public NotImplementedException(Throwable cause) {
        super(KEY, cause);
    }

    public NotImplementedException(String message, Throwable cause) {
        super(KEY, message, cause);
    }

    public NotImplementedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(KEY, message, cause, enableSuppression, writableStackTrace);
    }

}
