package br.gft.template.demo.exceptions;

public class NoContentException extends BaseException {

    public static final String KEY = "exceptions.rest.no_content";

    public NoContentException() {
        super(KEY);
    }

    public NoContentException(String message) {
        super(KEY, message);
    }

    public NoContentException(Throwable cause) {
        super(KEY, cause);
    }

    public NoContentException(String message, Throwable cause) {
        super(KEY, message, cause);
    }

    public NoContentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(KEY, message, cause, enableSuppression, writableStackTrace);
    }

}
