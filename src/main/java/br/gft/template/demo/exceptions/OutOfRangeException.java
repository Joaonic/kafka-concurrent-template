package br.gft.template.demo.exceptions;

public class OutOfRangeException extends BaseException {

    public static final String KEY = "exceptions.rest.out_of_range";

    public OutOfRangeException() {
        super(KEY);
    }

    public OutOfRangeException(String message) {
        super(KEY, message);
    }

    public OutOfRangeException(Throwable cause) {
        super(KEY, cause);
    }

    public OutOfRangeException(String message, Throwable cause) {
        super(KEY, message, cause);
    }

    public OutOfRangeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(KEY, message, cause, enableSuppression, writableStackTrace);
    }

}
