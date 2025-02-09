package br.gft.template.demo.exceptions;

public class FailedPreconditionException extends BaseException {

    public static final String KEY = "exceptions.rest.failed_precondition";

    public FailedPreconditionException() {
        super(KEY);
    }

    public FailedPreconditionException(String message) {
        super(KEY, message);
    }

    public FailedPreconditionException(Throwable cause) {
        super(KEY, cause);
    }

    public FailedPreconditionException(String message, Throwable cause) {
        super(KEY, message, cause);
    }

    public FailedPreconditionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(KEY, message, cause, enableSuppression, writableStackTrace);
    }

}
