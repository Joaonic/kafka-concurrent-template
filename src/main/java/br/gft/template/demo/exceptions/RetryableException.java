package br.gft.template.demo.exceptions;

public abstract class RetryableException extends BaseException {

    protected RetryableException() {
        super();
    }

    protected RetryableException(String key) {
        super(key);
    }

    protected RetryableException(String key, Throwable cause) {
        super(key, cause);
    }

    protected RetryableException(String key, String message) {
        super(key, message);
    }

    protected RetryableException(String key, String message, Throwable cause) {
        super(key, message, cause);
    }

    protected RetryableException(String key, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(key, message, cause, enableSuppression, writableStackTrace);
    }


}
