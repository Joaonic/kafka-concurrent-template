package br.gft.template.demo.exceptions;

import java.util.ResourceBundle;

public class BaseException extends RuntimeException {
    private static final ResourceBundle messages = ResourceBundle.getBundle("br.thinkgrowth.libraries.exceptions.messages");

    private String key;

    public BaseException() {
        super();
    }

    public BaseException(String key, Throwable cause) {
        super(cause);
        this.key = key;
    }

    public BaseException(String key, String message) {
        super(message);
        this.key = key;
    }

    public BaseException(String key, String message, Throwable cause) {
        super(message, cause);
        this.key = key;
    }

    public BaseException(String key, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.key = key;
    }

    public BaseException(String key) {
        super(messages.getString(key));
        this.key = key;
    }

    @Override
    public String getLocalizedMessage() {
        return messages.getString(key);
    }
}