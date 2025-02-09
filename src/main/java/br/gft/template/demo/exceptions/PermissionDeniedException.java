package br.gft.template.demo.exceptions;

public class PermissionDeniedException extends BaseException {

    public static final String KEY = "exceptions.rest.permission_denied";

    public PermissionDeniedException() {
        super(KEY);
    }

    public PermissionDeniedException(String message) {
        super(KEY, message);
    }

    public PermissionDeniedException(Throwable cause) {
        super(KEY, cause);
    }

    public PermissionDeniedException(String message, Throwable cause) {
        super(KEY, message, cause);
    }

    public PermissionDeniedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(KEY, message, cause, enableSuppression, writableStackTrace);
    }

}
