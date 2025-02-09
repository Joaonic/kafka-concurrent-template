package br.gft.template.demo.exceptions;

public class BadGatewayException extends BaseException {

    public static final String KEY = "exceptions.rest.bad_gateway";

    public BadGatewayException() {
        super(KEY);
    }

    public BadGatewayException(String message) {
        super(KEY, message);
    }

    public BadGatewayException(Throwable cause) {
        super(KEY, cause);
    }

    public BadGatewayException(String message, Throwable cause) {
        super(KEY, message, cause);
    }

    public BadGatewayException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(KEY, message, cause, enableSuppression, writableStackTrace);
    }

}
