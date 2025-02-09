package br.gft.template.demo.exceptions;

import java.util.ResourceBundle;

public abstract class BaseException extends RuntimeException {
    private static final ResourceBundle messages = ResourceBundle.getBundle("messages");

    private String key;

    protected BaseException() {
        super();
    }

    protected BaseException(String key, Throwable cause) {
        super(cause);
        this.key = key;
    }

    protected BaseException(String key, String message) {
        super(message);
        this.key = key;
    }

    protected BaseException(String key, String message, Throwable cause) {
        super(message, cause);
        this.key = key;
    }

    protected BaseException(String key, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.key = key;
    }

    protected BaseException(String key) {
        super(messages.getString(key));
        this.key = key;
    }

    @Override
    public String getLocalizedMessage() {
        return messages.getString(key);
    }
}