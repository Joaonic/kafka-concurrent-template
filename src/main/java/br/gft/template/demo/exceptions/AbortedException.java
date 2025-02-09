package br.gft.template.demo.exceptions;

public class AbortedException extends BaseException {

    public static final String KEY = "exceptions.rest.aborted";

    public AbortedException() {
        super(KEY);
    }

    public AbortedException(String message) {
        super(KEY, message);
    }

    public AbortedException(Throwable cause) {
        super(KEY, cause);
    }

    public AbortedException(String message, Throwable cause) {
        super(KEY, message, cause);
    }

    public AbortedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(KEY, message, cause, enableSuppression, writableStackTrace);
    }

}
