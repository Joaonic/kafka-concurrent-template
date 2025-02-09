package br.gft.template.demo.exceptions;

public class AuthenticationRequiredException extends BaseException {

    public static final String KEY = "exceptions.rest.authentication_required";

    public AuthenticationRequiredException() {
        super(KEY);
    }

    public AuthenticationRequiredException(String message) {
        super(KEY, message);
    }

    public AuthenticationRequiredException(Throwable cause) {
        super(KEY, cause);
    }

    public AuthenticationRequiredException(String message, Throwable cause) {
        super(KEY, message, cause);
    }

    public AuthenticationRequiredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(KEY, message, cause, enableSuppression, writableStackTrace);
    }

}
